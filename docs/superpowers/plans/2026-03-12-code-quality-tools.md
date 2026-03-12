# Code Quality Tools Integration Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Integrate JaCoCo, PMD, Checkstyle, SpotBugs, and Spotless into the Gradle build to enforce code quality, coverage metrics, and consistent formatting.

**Architecture:** Each tool is added as a Gradle plugin with its own configuration file under `config/`. All tools run during `./gradlew check`. Spotless runs separately via `./gradlew spotlessApply` (format) and `./gradlew spotlessCheck` (verify). Initial configurations are lenient to avoid blocking the build on existing code — thresholds can be tightened over time.

**Tech Stack:** Gradle 9.2.1 (Kotlin DSL), Java 17, JaCoCo 0.8.15, PMD 7.22.0, Checkstyle 10.26.1, SpotBugs plugin 6.4.8, Spotless 8.3.0

---

## File Structure

```
config/
├── checkstyle/
│   └── checkstyle.xml          # Checkstyle rules (Google style, relaxed)
├── pmd/
│   └── pmd-ruleset.xml         # PMD ruleset
└── spotbugs/
    └── spotbugs-exclude.xml    # SpotBugs exclusion filter
build.gradle.kts                # Modified: add plugins + configuration blocks
```

---

## Chunk 1: Plugin Registration and JaCoCo

### Task 1: Add all plugin declarations to build.gradle.kts

**Files:**
- Modify: `build.gradle.kts:3-9` (plugins block)

- [ ] **Step 1: Add plugins to the plugins block**

Add these plugins inside the existing `plugins { }` block:

```kotlin
plugins {
    java
    application
    idea
    jacoco
    pmd
    checkstyle
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.beryx.runtime") version "2.0.1"
    id("com.github.spotbugs") version "6.4.8"
    id("com.diffplug.spotless") version "8.3.0"
}
```

- [ ] **Step 2: Verify the build still compiles**

Run: `./gradlew help`
Expected: BUILD SUCCESSFUL (plugins resolve without errors)

- [ ] **Step 3: Commit**

```bash
git add build.gradle.kts
git commit -m "build: add code quality plugins (jacoco, pmd, checkstyle, spotbugs, spotless)"
```

### Task 2: Configure JaCoCo

**Files:**
- Modify: `build.gradle.kts` (append JaCoCo config)

- [ ] **Step 1: Add JaCoCo configuration block**

Append after the existing `tasks.test { ... }` block:

```kotlin
jacoco {
    toolVersion = "0.8.15"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.0".toBigDecimal() // Start at 0%, increase over time
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
```

- [ ] **Step 2: Run JaCoCo report**

Run: `./gradlew test jacocoTestReport`
Expected: BUILD SUCCESSFUL, HTML report at `build/reports/jacoco/test/html/index.html`

- [ ] **Step 3: Commit**

```bash
git add build.gradle.kts
git commit -m "build: configure JaCoCo coverage reporting (min 0%)"
```

---

## Chunk 2: PMD and Checkstyle

### Task 3: Create PMD ruleset and configure PMD

**Files:**
- Create: `config/pmd/pmd-ruleset.xml`
- Modify: `build.gradle.kts` (append PMD config)

- [ ] **Step 1: Create config directory**

Run: `mkdir -p config/pmd`

- [ ] **Step 2: Create PMD ruleset file**

Create `config/pmd/pmd-ruleset.xml`:

```xml
<?xml version="1.0"?>
<ruleset name="OeKBVisualClient"
         xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0
                             https://pmd.sourceforge.io/ruleset_2_0_0.xsd">

    <description>PMD rules for OeKBVisualClient</description>

    <!-- Best practices -->
    <rule ref="category/java/bestpractices.xml">
        <exclude name="GuardLogStatement"/>
        <exclude name="JUnitTestContainsTooManyAsserts"/>
    </rule>

    <!-- Code style -->
    <rule ref="category/java/codestyle.xml">
        <exclude name="AtLeastOneConstructor"/>
        <exclude name="CommentDefaultAccessModifier"/>
        <exclude name="LocalVariableCouldBeFinal"/>
        <exclude name="LongVariable"/>
        <exclude name="MethodArgumentCouldBeFinal"/>
        <exclude name="OnlyOneReturn"/>
        <exclude name="ShortVariable"/>
        <exclude name="TooManyStaticImports"/>
    </rule>

    <!-- Design -->
    <rule ref="category/java/design.xml">
        <exclude name="LawOfDemeter"/>
        <exclude name="LoosePackageCoupling"/>
        <exclude name="TooManyMethods"/>
    </rule>

    <!-- Error prone -->
    <rule ref="category/java/errorprone.xml">
        <exclude name="BeanMembersShouldSerialize"/>
        <exclude name="DataflowAnomalyAnalysis"/>
    </rule>

    <!-- Performance -->
    <rule ref="category/java/performance.xml"/>

</ruleset>
```

- [ ] **Step 3: Add PMD configuration to build.gradle.kts**

Append to `build.gradle.kts`:

```kotlin
pmd {
    toolVersion = "7.22.0"
    isConsoleOutput = true
    ruleSetFiles = files("config/pmd/pmd-ruleset.xml")
    ruleSets = listOf() // Use only our custom ruleset file
}

tasks.withType<Pmd>() {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

- [ ] **Step 4: Run PMD**

Run: `./gradlew pmdMain`
Expected: BUILD SUCCESSFUL (or violations listed in console + reports at `build/reports/pmd/`)

If the build fails due to violations, that's expected. Review the report and decide whether to fix violations or add exclusions.

- [ ] **Step 5: Commit**

```bash
git add config/pmd/pmd-ruleset.xml build.gradle.kts
git commit -m "build: configure PMD with relaxed ruleset for existing code"
```

### Task 4: Create Checkstyle config and configure Checkstyle

**Files:**
- Create: `config/checkstyle/checkstyle.xml`
- Modify: `build.gradle.kts` (append Checkstyle config)

- [ ] **Step 1: Create config directory**

Run: `mkdir -p config/checkstyle`

- [ ] **Step 2: Create Checkstyle configuration**

Create `config/checkstyle/checkstyle.xml`:

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="severity" value="warning"/>
    <property name="charset" value="UTF-8"/>
    <property name="fileExtension" value="java"/>

    <!-- File-level checks -->
    <module name="FileLength">
        <property name="max" value="1000"/>
    </module>
    <module name="FileTabCharacter"/>
    <module name="NewlineAtEndOfFile">
        <property name="lineSeparator" value="lf"/>
    </module>

    <module name="TreeWalker">
        <!-- Naming conventions -->
        <module name="ConstantName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="MethodName"/>
        <module name="PackageName"/>
        <module name="ParameterName"/>
        <module name="TypeName"/>

        <!-- Imports -->
        <module name="AvoidStarImport"/>
        <module name="RedundantImport"/>
        <module name="UnusedImports"/>

        <!-- Whitespace -->
        <module name="GenericWhitespace"/>
        <module name="MethodParamPad"/>
        <module name="NoWhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="ParenPad"/>
        <module name="WhitespaceAfter"/>
        <module name="WhitespaceAround">
            <property name="allowEmptyConstructors" value="true"/>
            <property name="allowEmptyMethods" value="true"/>
            <property name="allowEmptyTypes" value="true"/>
        </module>

        <!-- Blocks -->
        <module name="EmptyBlock"/>
        <module name="LeftCurly"/>
        <module name="NeedBraces"/>
        <module name="RightCurly"/>

        <!-- Coding -->
        <module name="EmptyStatement"/>
        <module name="EqualsHashCode"/>
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>

        <!-- Miscellaneous -->
        <module name="ArrayTypeStyle"/>
        <module name="UpperEll"/>

        <!-- Suppression via comments: // CHECKSTYLE:OFF / // CHECKSTYLE:ON -->
        <module name="SuppressionCommentFilter"/>
    </module>
</module>
```

- [ ] **Step 3: Add Checkstyle configuration to build.gradle.kts**

Append to `build.gradle.kts`:

```kotlin
checkstyle {
    toolVersion = "10.26.1"
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = true // Don't fail build initially
    maxWarnings = 0 // Set to a high number initially, then tighten
}

tasks.withType<Checkstyle>() {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

Note: Set `maxWarnings = 0` with `isIgnoreFailures = true` so violations appear in reports but don't block the build. Once existing violations are fixed, flip `isIgnoreFailures` to `false`.

- [ ] **Step 4: Run Checkstyle**

Run: `./gradlew checkstyleMain`
Expected: BUILD SUCCESSFUL, reports at `build/reports/checkstyle/`

- [ ] **Step 5: Commit**

```bash
git add config/checkstyle/checkstyle.xml build.gradle.kts
git commit -m "build: configure Checkstyle with standard rules (warnings only)"
```

---

## Chunk 3: SpotBugs and Spotless

### Task 5: Create SpotBugs exclusion filter and configure SpotBugs

**Files:**
- Create: `config/spotbugs/spotbugs-exclude.xml`
- Modify: `build.gradle.kts` (append SpotBugs config)

- [ ] **Step 1: Create config directory**

Run: `mkdir -p config/spotbugs`

- [ ] **Step 2: Create SpotBugs exclusion filter**

Create `config/spotbugs/spotbugs-exclude.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<FindBugsFilter>
    <!-- Exclude JavaFX controller classes from serialization checks -->
    <Match>
        <Package name="controller"/>
        <Bug pattern="SE_BAD_FIELD"/>
    </Match>

    <!-- Exclude generated FXML bindings -->
    <Match>
        <Bug pattern="UWF_UNWRITTEN_FIELD"/>
        <Package name="controller"/>
    </Match>

    <!-- Exclude test classes -->
    <Match>
        <Source name="~.*Test\.java"/>
    </Match>
</FindBugsFilter>
```

- [ ] **Step 3: Add SpotBugs configuration to build.gradle.kts**

First, add the import at the top of `build.gradle.kts`:

```kotlin
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
```

Then append configuration:

```kotlin
spotbugs {
    effort.set(Effort.DEFAULT)
    reportLevel.set(Confidence.DEFAULT)
    excludeFilter.set(file("config/spotbugs/spotbugs-exclude.xml"))
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>() {
    reports.create("html") {
        required.set(true)
    }
    reports.create("xml") {
        required.set(true)
    }
}
```

- [ ] **Step 4: Run SpotBugs**

Run: `./gradlew spotbugsMain`
Expected: BUILD SUCCESSFUL, report at `build/reports/spotbugs/`

- [ ] **Step 5: Commit**

```bash
git add config/spotbugs/spotbugs-exclude.xml build.gradle.kts
git commit -m "build: configure SpotBugs with exclusions for JavaFX controllers"
```

### Task 6: Configure Spotless

**Files:**
- Modify: `build.gradle.kts` (append Spotless config)

- [ ] **Step 1: Add Spotless configuration to build.gradle.kts**

Append to `build.gradle.kts`:

```kotlin
spotless {
    java {
        target("src/*/java/**/*.java")
        // Use Google Java Format (AOSP style = 4-space indent, matching project convention)
        googleJavaFormat("1.25.2").aosp()
        // Remove unused imports
        removeUnusedImports()
        // Trim trailing whitespace
        trimTrailingWhitespace()
        // Ensure newline at end of file
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
```

- [ ] **Step 2: Check current formatting state**

Run: `./gradlew spotlessCheck`
Expected: May fail with formatting violations (that's expected)

- [ ] **Step 3: Auto-format existing code**

Run: `./gradlew spotlessApply`
Expected: BUILD SUCCESSFUL, files reformatted in-place

- [ ] **Step 4: Verify formatting is clean**

Run: `./gradlew spotlessCheck`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Verify the build still passes after formatting**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit formatted code**

```bash
git add -A
git commit -m "style: apply Spotless formatting to all Java sources"
```

---

## Chunk 4: Integration and .gitignore

### Task 7: Verify full check pipeline

**Files:**
- Modify: `build.gradle.kts` (if adjustments needed)

- [ ] **Step 1: Run the complete check pipeline**

Run: `./gradlew clean check`
Expected: BUILD SUCCESSFUL (all tools run as part of `check`)

If any tool fails the build, review the report and either:
- Fix the violation if trivial
- Add an exclusion/suppression for legitimate patterns
- Set `isIgnoreFailures = true` temporarily

- [ ] **Step 2: Review generated reports**

Reports should be at:
- `build/reports/jacoco/test/html/index.html` — Coverage
- `build/reports/pmd/main.html` — PMD violations
- `build/reports/checkstyle/main.html` — Checkstyle violations
- `build/reports/spotbugs/main.html` — SpotBugs findings

- [ ] **Step 3: Update .gitignore if needed**

Ensure `build/` is already in `.gitignore` (reports are generated there).

- [ ] **Step 4: Final commit**

```bash
git add build.gradle.kts .gitignore
git commit -m "build: finalize code quality tool integration"
```

### Task 8: Update CLAUDE.md

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: Add code quality section to CLAUDE.md**

Add a new section after "## Quick Start":

```markdown
### Code Quality Checks
```bash
# Run all checks (tests + JaCoCo + PMD + Checkstyle + SpotBugs)
./gradlew check

# Format code with Spotless
./gradlew spotlessApply

# Check formatting without fixing
./gradlew spotlessCheck

# Individual tool reports
./gradlew jacocoTestReport    # Coverage → build/reports/jacoco/
./gradlew pmdMain             # PMD → build/reports/pmd/
./gradlew checkstyleMain      # Checkstyle → build/reports/checkstyle/
./gradlew spotbugsMain        # SpotBugs → build/reports/spotbugs/
```

Configuration files are in `config/` directory.
```

- [ ] **Step 2: Commit**

```bash
git add CLAUDE.md
git commit -m "docs: add code quality tool commands to CLAUDE.md"
```

---

## Summary of Tool Versions

| Tool       | Version   | Plugin ID                  | Config File                          |
|------------|-----------|----------------------------|--------------------------------------|
| JaCoCo     | 0.8.15    | `jacoco` (built-in)        | In `build.gradle.kts`               |
| PMD        | 7.22.0    | `pmd` (built-in)           | `config/pmd/pmd-ruleset.xml`        |
| Checkstyle | 10.26.1   | `checkstyle` (built-in)    | `config/checkstyle/checkstyle.xml`  |
| SpotBugs   | 6.4.8     | `com.github.spotbugs`      | `config/spotbugs/spotbugs-exclude.xml` |
| Spotless   | 8.3.0     | `com.diffplug.spotless`    | In `build.gradle.kts`               |

## Design Decisions

1. **All tools start lenient** — `isIgnoreFailures = true` for Checkstyle, minimum coverage 0%. This prevents blocking development on existing violations. Tighten gradually.
2. **Spotless uses Google Java Format (AOSP)** — AOSP variant uses 4-space indentation matching the project's existing style.
3. **SpotBugs excludes JavaFX controllers** — Controller classes have FXML-injected fields that trigger false positives for unwritten fields and serialization warnings.
4. **PMD excludes opinionated rules** — Rules like `LawOfDemeter`, `OnlyOneReturn`, `AtLeastOneConstructor` are excluded as they don't match the project's style.
5. **Config files in `config/` directory** — Keeps root directory clean, standard Gradle convention.
