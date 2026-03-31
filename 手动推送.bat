cd /d C:\Users\ygs\Desktop\FocusBloom

git add .github/workflows/build.yml

git rm -f .github/workflows/main.yml 2>nul

git commit -m "Fix workflow"

git push origin main

pause