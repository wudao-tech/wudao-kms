# Base image
FROM registry.cn-hangzhou.aliyuncs.com/wudaotech/openjdk:21.2

# Maintainer
LABEL maintainer=soho

# Set working directory
WORKDIR /home/wudao

# Switch to root user
USER root

# Create necessary directories (fixed duplicates)
RUN mkdir -p /home/wudao/uploadPath/download/ \
             /home/wudao/uploadPath/temp/ \
             /home/wudao/logs/

# Copy application jar
COPY kms-server/target/kms-server.jar /home/wudao/kms-server.jar

# Exposing the application port
EXPOSE 8090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8090/kms/actuator/health || exit 1

# Entrypoint with corrected JVM arguments order
# All -D parameters must come BEFORE -jar
ENTRYPOINT ["java", \
    "-Duser.timezone=GMT+8", \
    "-Dfile.encoding=UTF-8", \
    "-Xms2g", \
    "-Xmx2g", \
    "-Xlog:gc*:file=/home/wudao/logs/gc.log:time,uptime:filecount=10,filesize=10M", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-XX:HeapDumpPath=/home/wudao/logs/heapdump.hprof", \
    "-jar", "/home/wudao/kms-server.jar"]

