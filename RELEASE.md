# Release Process

## Creating a New Release

This project uses GitHub Actions to automatically build Windows installers when you create a release.

### Step 1: Update Version

Edit `build.gradle.kts` and update the version:
```kotlin
version = "0.5"  // Change this
```

### Step 2: Commit and Push

```bash
git add build.gradle.kts
git commit -m "Bump version to 0.5"
git push
```

### Step 3: Create Git Tag

```bash
git tag v0.5
git push origin v0.5
```

### Step 4: Create GitHub Release

1. Go to: https://github.com/karlkauc/OeKBVisualClient/releases/new
2. Choose tag: `v0.5`
3. Release title: `Version 0.5`
4. Write release notes (what's new, bug fixes, etc.)
5. Click **"Publish release"**

### Step 5: Wait for Build

- GitHub Actions automatically starts building
- Check progress: https://github.com/karlkauc/OeKBVisualClient/actions
- Build takes approximately 10-15 minutes
- MSI installer is automatically uploaded to the release

### Step 6: Verify

Once the build completes:
1. Go to the release page
2. Download the MSI file: `OeKBVisualClient-0.5.msi`
3. Test installation on Windows

## Manual Build Trigger

You can manually trigger a build without creating a release:

1. Go to: https://github.com/karlkauc/OeKBVisualClient/actions
2. Select **"Build Windows Installer"**
3. Click **"Run workflow"**
4. Choose branch (usually `master`)
5. Click **"Run workflow"**

The installer will be available as a build artifact (not attached to a release).

## Version Numbering

Follow semantic versioning:
- **Major**: Breaking changes (e.g., 1.0, 2.0)
- **Minor**: New features (e.g., 0.4, 0.5)
- **Patch**: Bug fixes (e.g., 0.4.1, 0.4.2)

## Release Checklist

Before creating a release:

- [ ] Update version in `build.gradle.kts`
- [ ] Test application locally (`./gradlew run`)
- [ ] Update CHANGELOG (if you have one)
- [ ] Commit all changes
- [ ] Create and push tag
- [ ] Write meaningful release notes
- [ ] Wait for GitHub Actions to complete
- [ ] Test the generated MSI installer

## Troubleshooting

### Build Fails on GitHub Actions

Check the Actions log:
1. Go to: https://github.com/karlkauc/OeKBVisualClient/actions
2. Click on the failed workflow
3. Expand the failed step
4. Look for error messages

Common issues:
- **Missing dependencies**: Check `build.gradle.kts`
- **Compilation errors**: Test locally first
- **WiX Toolset errors**: Windows-specific, check installer options

### Installer Not Uploaded

If the build succeeds but no MSI appears:
1. Check the "Find installer files" step in Actions log
2. Verify the file path matches `build/jpackage/*.msi`
3. Check if `jpackage` task completed successfully

## Rolling Back

If a release has issues:

1. Mark the release as "Pre-release" on GitHub
2. Create a new release with fixes
3. Once verified, delete the problematic release

Never delete a tag that users might have downloaded!
