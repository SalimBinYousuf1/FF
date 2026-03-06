# 🚀 VibePlayer — Push to GitHub & Get APK Automatically

## What happens automatically
Every single time you `git push`, GitHub Actions will:
1. ✅ Check out your code
2. ✅ Set up Java 17
3. ✅ Download Gradle wrapper JAR if missing (self-healing)
4. ✅ Cache all dependencies (fast subsequent builds)
5. ✅ Build the Debug APK
6. ✅ Upload it as a downloadable artifact

**No clicks needed. No setup needed on GitHub's side.**

---

## Step 1 — Create a GitHub repository

```bash
# On GitHub.com → New Repository → name it "VibePlayer" → Create
```

---

## Step 2 — Push this project

```bash
cd VibePlayer
git init
git add .
git commit -m "🎵 Initial VibePlayer commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/VibePlayer.git
git push -u origin main
```

That's it. GitHub Actions starts building **immediately**.

---

## Step 3 — Download your APK

1. Go to your repository on GitHub
2. Click the **Actions** tab
3. Click the latest workflow run
4. Scroll to **Artifacts** at the bottom
5. Download **VibePlayer-debug-build-1**
6. Unzip → install the `.apk` on your Android device

---

## Making a versioned Release (optional)

Push a version tag to create a proper GitHub Release with APK attached:

```bash
git tag v1.0.0
git push origin v1.0.0
```

→ GitHub will create a Release at `github.com/YOUR_USERNAME/VibePlayer/releases`  
→ The APK is attached directly for easy download by anyone

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `gradlew: Permission denied` | The workflow auto-fixes this with `chmod +x` |
| `gradle-wrapper.jar not found` | The workflow auto-downloads Gradle and regenerates it |
| `SDK licenses not accepted` | The workflow auto-accepts them |
| Build fails with OOM | Already handled — `GRADLE_OPTS` limits memory usage |
| Workflow doesn't trigger | Make sure you pushed to `main`, `master`, or `develop` |

