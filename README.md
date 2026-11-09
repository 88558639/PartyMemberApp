# 党员信息查询系统

一个基于Android的党员信息查询应用，支持通过姓氏联想搜索、Excel批量导入功能，采用现代毛玻璃UI设计。

## 功能特性

### 🔍 智能搜索
- 输入姓氏或姓名开头即可联想匹配的姓名
- 实时搜索结果展示
- 按姓名前缀匹配（不支持中间字匹配）

### 📊 数据管理
- 支持通过Excel文件批量导入数据
- 支持Excel格式说明和校验
- 数据持久化存储

### 🎨 现代UI设计
- 毛玻璃效果界面
- 渐变背景
- 流畅的动画效果
- 适配深色主题

## 技术栈

- **语言**: Kotlin
- **UI框架**: Material Design 3
- **数据库**: Room Database
- **异步处理**: Kotlin Coroutines + Flow
- **Excel处理**: Apache POI
- **最低SDK**: Android 8.0 (API 26)
- **目标SDK**: Android 14 (API 34)

## 项目结构

```
app/src/main/java/com/example/partymember/
├── data/                    # 数据层
│   ├── PartyMember.kt      # 数据实体
│   ├── PartyMemberDao.kt   # 数据访问对象
│   ├── AppDatabase.kt      # 数据库配置
│   └── PartyMemberRepository.kt  # 数据仓库
├── ui/                      # UI层
│   ├── MainActivity.kt      # 主页
│   ├── DetailActivity.kt    # 详情页
│   ├── UploadActivity.kt   # 上传页
│   ├── MainViewModel.kt     # 主页ViewModel
│   ├── UploadViewModel.kt   # 上传ViewModel
│   └── PartyMemberAdapter.kt  # 列表适配器
└── utils/                   # 工具类
    └── ExcelParser.kt       # Excel解析器
```

## 导入用的 Excel 文件在哪里

App 不内置任何真实党员名册，导入用的 Excel（下称「目标 Excel」）需要自行准备。三个入口：

| 需要什么 | 位置 / 获取方式 |
|---|---|
| 空白模板（源码） | `app/src/main/assets/template.xlsx`，仓库内可直接下载；仅含 8 列表头 + 1 条虚构示例数据 |
| 空白模板（手机上） | 打开 App → 右上角导入图标 → 页面底部「下载模板」→ 保存到手机「下载 / Download」目录，文件名为 `党员信息导入模板.xlsx`（Android 10+ 经 MediaStore.Downloads 写入，旧版写入 `Download/`） |
| 填好数据的目标 Excel | 把模板传到电脑填写后再放回手机**任意可被文件选择器访问的位置**即可；不要求固定目录、不要求改回原文件名、不需要放进 APK |

导入方式：在导入页点「选择文件」直接选中该 `.xlsx` / `.xls`，**选完即自动导入**（覆盖式，先清空再写入），无需再点上传。列顺序固定为 8 列，详见下方「Excel格式要求」与 `Excel导入说明.md`。

> Release 中只提供 APK 安装包，**不附带 Excel 数据文件**；仓库与 APK 内的模板均只有虚构示例数据，真实名册不会上传、也不会随包分发。

## Excel格式要求

导入的 Excel 按**固定列顺序**解析（共 8 列，首行表头自动跳过）：

| 列 | 列名 | 是否必填 | 示例 |
|---|---|---|---|
| A | 姓名 | 必填 | 张三 |
| B | 性别 | 选填 | 男 |
| C | 地址 | 选填 | 北京市朝阳区... |
| D | 电话 | 选填 | 13800138000 |
| E | 党费 | 选填 | 50.00 |
| F | 所属支部 | 选填 | 第一党支部 |
| G | 身份证 | 选填 | 18 位，建议存为文本 |
| H | 年龄 | 选填 | 45 |

**注意事项**:
- 文件格式: `.xlsx` 或 `.xls`，导入为**覆盖式**（先清空再写入）
- 第一行应为列标题，姓名为空的行会跳过
- 电话、身份证建议存为文本，避免被 Excel 转成科学计数法

## 安装与运行

1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器
4. 点击运行按钮

## 使用说明

### 搜索党员信息
1. 打开应用，在搜索框中输入姓氏或姓名开头
2. 系统按前缀匹配实时显示结果
3. 点击列表项查看详细信息

### 导入Excel数据
1. 点击右上角导入图标
2. 点击「选择文件」，从设备中选择 `.xlsx` 或 `.xls`
3. **选完即自动导入**，无需再点上传；完成后自动返回列表
4. 同一页面底部还可「下载模板」「清空数据」

### 查看详情
- 在搜索结果列表中点击任意党员条目
- 查看 8 个字段：姓名、性别、地址、电话、党费、所属支部、身份证、年龄

## 开发环境

- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Gradle 8.2
- Kotlin 1.9.0

## 依赖库

- AndroidX Core
- Material Components
- Room Database
- Kotlin Coroutines
- Apache POI (Excel处理)

## 许可证

本项目仅供学习和交流使用。
