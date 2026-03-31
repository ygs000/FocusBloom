@echo off
cd /d "C:\Users\ygs\Desktop\FocusBloom"

echo 正在添加新的workflow文件...
git add .github/workflows/build.yml

echo 正在删除错误的文件...
git rm -f .github/workflows/main.yml 2>nul

echo 提交更改...
git commit -m "Fix GitHub Actions workflow"

echo 推送到GitHub...
git push origin main

echo.
echo 完成！请刷新GitHub页面查看Actions
echo 链接: https://github.com/ygs000/FocusBloom/actions
pause