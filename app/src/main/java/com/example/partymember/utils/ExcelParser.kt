package com.example.partymember.utils

import android.content.Context
import com.example.partymember.data.PartyMember
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.ByteArrayInputStream
import java.io.InputStream

class ExcelParser(private val context: Context) {

    fun parseExcel(inputStream: InputStream): Result<List<PartyMember>> {
        return try {
            val bytes = inputStream.use { it.readBytes() }
            val workbook = if (bytes.size >= 4 &&
                bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte() &&
                bytes[2] == 0x03.toByte() && bytes[3] == 0x04.toByte()
            ) {
                XSSFWorkbook(ByteArrayInputStream(bytes))
            } else {
                HSSFWorkbook(ByteArrayInputStream(bytes))
            }

            val sheet = workbook.getSheetAt(0)
            val members = mutableListOf<PartyMember>()

            // Skip header row, start from row 1
            for (rowIndex in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(rowIndex) ?: continue

                try {
                    val name = getCellStringValue(row.getCell(0))
                    val gender = getCellStringValue(row.getCell(1))
                    val address = getCellStringValue(row.getCell(2))
                    val phone = getCellStringValue(row.getCell(3))
                    val partyDues = getCellMoneyValue(row.getCell(4))
                    val partyBranch = getCellStringValue(row.getCell(5))
                    val idCard = getCellStringValue(row.getCell(6))
                    val age = getCellAgeValue(row.getCell(7))

                    if (name.isNotEmpty()) {
                        members.add(
                            PartyMember(
                                name = name,
                                gender = gender,
                                address = address,
                                phone = phone,
                                partyDues = partyDues,
                                partyBranch = partyBranch,
                                idCard = idCard,
                                age = age
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Skip invalid rows
                    continue
                }
            }

            workbook.close()
            Result.success(members)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCellStringValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> {
                // 处理数字类型，电话号码都是整数，强制转换为字符串避免科学计数法
                val numericValue = cell.numericCellValue
                if (numericValue == 0.0) {
                    "0"
                } else {
                    // 将数字转换为长整型再转为字符串
                    java.math.BigDecimal(numericValue).toPlainString()
                }
            }
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> try {
                cell.stringCellValue.trim()
            } catch (e: Exception) {
                ""
            }
            CellType.BLANK -> ""
            else -> ""
        }
    }

    private fun getCellAgeValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> {
                val numericValue = cell.numericCellValue
                // 年龄取整
                if (numericValue == numericValue.toLong().toDouble()) {
                    numericValue.toLong().toString()
                } else {
                    java.math.BigDecimal(numericValue).setScale(0, java.math.BigDecimal.ROUND_HALF_UP).toPlainString()
                }
            }
            CellType.FORMULA -> try {
                val formulaResult = cell.cachedFormulaResultType
                when (formulaResult) {
                    CellType.NUMERIC -> {
                        val numericValue = cell.numericCellValue
                        if (numericValue == numericValue.toLong().toDouble()) {
                            numericValue.toLong().toString()
                        } else {
                            java.math.BigDecimal(numericValue).setScale(0, java.math.BigDecimal.ROUND_HALF_UP).toPlainString()
                        }
                    }
                    CellType.STRING -> cell.stringCellValue.trim()
                    else -> ""
                }
            } catch (e: Exception) {
                ""
            }
            CellType.BLANK -> ""
            else -> ""
        }
    }

    private fun getCellMoneyValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> {
                // 处理金额，保留2位小数
                val numericValue = cell.numericCellValue
                if (numericValue == 0.0) {
                    "0.00"
                } else {
                    // 保留2位小数
                    java.math.BigDecimal(numericValue).setScale(2, java.math.BigDecimal.ROUND_HALF_UP).toPlainString()
                }
            }
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> try {
                cell.stringCellValue.trim()
            } catch (e: Exception) {
                ""
            }
            CellType.BLANK -> ""
            else -> ""
        }
    }
}
