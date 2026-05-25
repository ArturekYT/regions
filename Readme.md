# Regions API

## Instalacja
### Gradle (build.gradle)

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

```gradle
dependencies {
    compileOnly 'com.github.ArturekYT:regions:1.0'
}
```

### Maven (pom.xml)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.ArturekYT</groupId>
    <artifactId>regions</artifactId>
    <version>1.0</version>
    <scope>provided</scope>
</dependency>
```

## Przykłady użycia API

```java
public static boolean isInRegion(Player player, String... regionNames) {
    RegionManager regionManager = new RegionManager(Main.getInstance());
    return regionManager.isInRegions(player, regionNames);
}

public static boolean isIn(Location location, Set<String> regions) {
    RegionManager regionManager = new RegionManager(Main.getInstance());
    return regionManager.getRegionNamesAtLocation(location).equals(regions);
}

```

## Komendy pluginu

Dostępne komendy:

* `/region selector` - Nadaje przedmiot do zaznaczania punktów (pos1 i pos2).
* `/region create <region> <priority>` - Tworzy region z określonym priorytetem.
* `/region remove <region>` - Usuwa istniejący region.
* `/region flag <region> <flag> <boolean>` - Dodaje bądź zmienia flagę dla regionu.
* `/region show-flags <region>` - Wyświetla listę flag przypisanych do regionu.
* `/region reload` - Przeładowuje konfigurację pluginu.

## Licencja

Projekt dostępny na licencji **MIT**.