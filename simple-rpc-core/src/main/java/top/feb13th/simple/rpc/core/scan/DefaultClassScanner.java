package top.feb13th.simple.rpc.core.scan;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.SneakyThrows;
import top.feb13th.simple.rpc.core.util.Strings;

/**
 * 类文件扫描器
 *
 * @author feb13th
 */
public class DefaultClassScanner implements ClassScanner {

  @Override
  @SneakyThrows
  public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate) {
    if (Strings.isBlank(packageName)) {
      throw new NullPointerException("packageName is null");
    }
    if (predicate == null) {
      throw new NullPointerException("predicate is null");
    }

    Path rootPath = Paths.get(new File(getFilePath(packageName)).toURI());
    // 扫描到的所有类
    Set<Class<?>> classes = new LinkedHashSet<>();

    Enumeration<URL> resources = Thread.currentThread()
        .getContextClassLoader()
        .getResources(packageName.replace(".", "/"));
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      String protocol = url.getProtocol();
      if ("jar".equalsIgnoreCase(protocol)) {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        if (connection == null) {
          continue;
        }
        JarFile jarFile = connection.getJarFile();
        if (jarFile == null) {
          continue;
        }
        scanJar(classes, jarFile, packageName, predicate);
      } else {
        scanPath(classes, rootPath, packageName, predicate);
      }
    }

    return classes;
  }

  @SneakyThrows
  private void scanJar(Set<Class<?>> container, JarFile jarFile, String packageName, Predicate<Class<?>> predicate) {
    Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      JarEntry jarEntry = entries.nextElement();
      String entryName = jarEntry.getName();
      if (!entryName.endsWith(ClassScanner.CLASS_SUFFIX) || !entryName.replaceAll("/", ".").startsWith(packageName)) {
        continue;
      }
      String className = entryName.substring(0, entryName.indexOf("."));
      Class<?> clazz = Class.forName(className.replace("/", "."));
      if (predicate.test(clazz)) {
        container.add(clazz);
      }
    }
  }

  @SneakyThrows
  private void scanPath(Set<Class<?>> container, Path root, String packageName, Predicate<Class<?>> predicate) {
    if (Files.isDirectory(root)) {
      Files.list(root).forEach(path -> {
        String filename = path.getFileName().toString();
        if (Files.isDirectory(path)) {
          String newPackageName = packageName + "." + filename;
          scanPath(container, path, newPackageName, predicate);
          return;
        }
        parseOneClassFile(container, packageName, predicate, path, filename);
      });
    } else {
      String filename = root.getFileName().toString();
      parseOneClassFile(container, packageName, predicate, root, filename);
    }
  }

  @SneakyThrows
  private void parseOneClassFile(Set<Class<?>> container, String packageName, Predicate<Class<?>> predicate, Path path,
      String filename) {
    if (!path.getFileName().toString().endsWith(ClassScanner.CLASS_SUFFIX)) {
      return;
    }
    String className = filename.substring(0, filename.indexOf("."));
    Class<?> clazz = Class.forName(packageName + "." + className);
    if (predicate.test(clazz)) {
      container.add(clazz);
    }
  }

  @SneakyThrows
  private String getFilePath(String packageName) {
    String packagePath = packageName.replace(".", "/");
    String path = DefaultClassScanner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    File file = new File(path);
    if (file.isFile()) {
      // 可能运行的是jar
      path = file.getParent();
    }
    return path + packagePath;
  }
}
