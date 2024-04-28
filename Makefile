# 项目名称
PROJECT_NAME := aps

# Maven命令
MAVEN_CMD := mvn

# Maven目标
MAVEN_TARGETS := clean package

# 打包后的JAR文件路径
JAR_PATH := target/$(PROJECT_NAME)-*.jar

# 定义目标
.PHONY: all clean build run

# 默认目标：执行所有步骤（先清理再构建）
all: clean build

# 清理目标：使用Maven清理项目
clean:
	$(MAVEN_CMD) $(MAVEN_TARGETS) -DskipTests clean

# 构建目标：使用Maven构建项目
build:
	$(MAVEN_CMD) $(MAVEN_TARGETS) -DskipTests

# 运行目标：使用Java运行打包后的JAR文件
run:
	java -jar $(JAR_PATH)