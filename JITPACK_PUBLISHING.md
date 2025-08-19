# Publishing to JitPack - Complete Guide

This guide will help you publish your LimeChat Android SDK to JitPack so other developers can easily integrate it into their projects.

## Prerequisites

1. **GitHub Repository**: Your code must be in a public GitHub repository
2. **Git Setup**: Ensure you have git configured with your credentials
3. **Java 17**: Required for building the project
4. **Gradle**: The project uses Gradle for building

## Quick Start

### 1. Automated Publishing (Recommended)

Use the provided script for easy publishing:

```bash
./publish-to-jitpack.sh
```

The script will:
- Check for uncommitted changes
- Update the version number
- Build and test the project
- Create a git tag
- Push to GitHub
- Provide next steps

### 2. Manual Publishing

If you prefer to publish manually, follow these steps:

#### Step 1: Update Version

Edit `library/build.gradle` and update the version:

```gradle
version = '1.0.1' // or your desired version
```

#### Step 2: Build and Test

```bash
./gradlew clean
./gradlew :library:assembleRelease
./gradlew :library:test
```

#### Step 3: Commit and Tag

```bash
git add library/build.gradle
git commit -m "Bump version to 1.0.1"
git tag -a "v1.0.1" -m "Release version 1.0.1"
git push origin main
git push origin "v1.0.1"
```

## Configuration Files

### jitpack.yml

This file tells JitPack how to build your project:

```yaml
jdk:
  - openjdk17

before_install:
  - ./gradlew clean

install:
  - ./gradlew :library:assembleRelease
  - ./gradlew :library:generatePomFileForReleasePublication
  - ./gradlew :library:publishReleasePublicationToMavenLocal

script:
  - ./gradlew :library:test
  - ./gradlew :library:assembleRelease
  - ./gradlew :library:generatePomFileForReleasePublication
  - ./gradlew :library:publishReleasePublicationToMavenLocal
```

### build.gradle (Library Module)

The publishing configuration in `library/build.gradle`:

```gradle
afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                
                groupId = 'com.github.limechat'
                artifactId = 'limechat-android-sdk'
                version = '1.0.0'
                
                pom {
                    name = 'LimeChat Android SDK'
                    description = 'Official Android SDK for LimeChat widget integration'
                    url = 'https://github.com/limechat/limechat-android-sdk'
                    
                    licenses {
                        license {
                            name = 'MIT License'
                            url = 'https://opensource.org/licenses/MIT'
                        }
                    }
                    
                    developers {
                        developer {
                            id = 'limechat'
                            name = 'LimeChat Team'
                            email = 'support@limechat.com'
                        }
                    }
                    
                    scm {
                        connection = 'scm:git:git://github.com/limechat/limechat-android-sdk.git'
                        developerConnection = 'scm:git:ssh://github.com:limechat/limechat-android-sdk.git'
                        url = 'https://github.com/limechat/limechat-android-sdk'
                    }
                }
            }
        }
    }
}
```

## Important Configuration Notes

### 1. Repository Name

Make sure your GitHub repository name matches what you specify in the artifact coordinates. If your repository is named `limechat-android-sdk`, users will reference it as:

```gradle
implementation 'com.github.YourUsername:limechat-android-sdk:1.0.0'
```

### 2. Group ID

The group ID should follow the pattern `com.github.YourUsername` where `YourUsername` is your actual GitHub username.

### 3. Version Management

- Use semantic versioning (e.g., 1.0.0, 1.0.1, 1.1.0)
- Each version should have a corresponding git tag
- Tags should be in the format `v1.0.0` (with the 'v' prefix)

## Publishing Process

### 1. Create a Release

After pushing your tag, JitPack will automatically detect it and start building:

1. Go to https://jitpack.io/#YourUsername/YourRepo
2. You'll see your tag listed
3. Click "Get it" to build the release
4. Wait for the build to complete (usually 2-5 minutes)

### 2. Verify the Build

Check the build logs for any errors. Common issues include:
- Missing dependencies
- Build configuration errors
- Test failures

### 3. Test the Dependency

Create a simple test project to verify the dependency works:

```gradle
// In your test project's build.gradle
dependencies {
    implementation 'com.github.YourUsername:YourRepo:1.0.0'
}
```

## Troubleshooting

### Common Issues

#### 1. Build Fails on JitPack

**Symptoms**: Build fails with compilation errors
**Solutions**:
- Check that all dependencies are available
- Ensure your `jitpack.yml` is correct
- Verify that the project builds locally

#### 2. Dependency Not Found

**Symptoms**: Gradle can't resolve the dependency
**Solutions**:
- Wait for the JitPack build to complete
- Check the exact artifact coordinates
- Ensure the repository is public

#### 3. Version Not Available

**Symptoms**: Specific version not found
**Solutions**:
- Check that the git tag exists
- Verify the tag format (should be `v1.0.0`)
- Wait for JitPack to process the tag

#### 4. AAR File Issues

**Symptoms**: AAR file not generated or corrupted
**Solutions**:
- Check the `android` block configuration
- Ensure `apply plugin: 'maven-publish'` is included
- Verify the publishing configuration

### Debugging Steps

1. **Local Build Test**:
   ```bash
   ./gradlew clean
   ./gradlew :library:assembleRelease
   ./gradlew :library:test
   ```

2. **Check JitPack Logs**:
   - Go to your JitPack page
   - Click on the build log
   - Look for error messages

3. **Verify Configuration**:
   - Check `jitpack.yml` syntax
   - Verify `build.gradle` publishing config
   - Ensure all required files are committed

## Best Practices

### 1. Version Management

- Use semantic versioning
- Create meaningful release notes
- Tag releases immediately after testing

### 2. Testing

- Always test locally before publishing
- Create a simple test project to verify the dependency
- Test with both Kotlin and Java projects

### 3. Documentation

- Keep README.md updated
- Include usage examples
- Document breaking changes

### 4. Quality Assurance

- Run tests before publishing
- Check for lint warnings
- Verify ProGuard compatibility

## Example Workflow

Here's a complete example of publishing version 1.0.1:

```bash
# 1. Update version
sed -i.bak "s/version = '1.0.0'/version = '1.0.1'/g" library/build.gradle

# 2. Build and test
./gradlew clean
./gradlew :library:assembleRelease
./gradlew :library:test

# 3. Commit and tag
git add library/build.gradle
git commit -m "Bump version to 1.0.1"
git tag -a "v1.0.1" -m "Release version 1.0.1"
git push origin main
git push origin "v1.0.1"

# 4. Wait for JitPack build (2-5 minutes)
# 5. Test the dependency in a new project
```

## Support

If you encounter issues:

1. Check the [JitPack documentation](https://jitpack.io/docs/)
2. Review the build logs on JitPack
3. Test locally to isolate issues
4. Check GitHub issues for similar problems

## Next Steps

After successful publishing:

1. Update your documentation with the new version
2. Announce the release to your users
3. Monitor for any issues
4. Plan the next release

Remember: JitPack builds are cached, so subsequent builds of the same version will be faster.
