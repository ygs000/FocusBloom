# FocusBloom

一款**100%免费、无广告、无内购、完全离线**的安卓番茄钟+任务管理应用。

## 特性

- ✅ 100% 免费，无广告，无内购
- ✅ 完全离线，无需网络
- ✅ 番茄钟专注计时
- ✅ 任务管理
- ✅ 深色模式支持
- ✅ 后台计时保活
- ✅ Material Design 3

## 下载安装

### 从 GitHub Releases 下载

1. 访问 [Releases 页面](../../releases)
2. 下载最新版本的 APK 文件
3. 在安卓手机上安装即可使用

### 自动构建

每次推送到 main 分支时，GitHub Actions 会自动构建 APK 并上传到 Artifacts。

## 技术栈

- Kotlin 100%
- Jetpack Compose
- Room 数据库
- Hilt 依赖注入
- MVVM 架构

## 自行构建

```bash
# 克隆仓库
git clone https://github.com/yourusername/focusbloom.git
cd focusbloom

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

生成的 APK 位于 `app/build/outputs/apk/` 目录下。

## 许可证

MIT License

## 致谢

感谢所有为这个开源项目做出贡献的开发者。