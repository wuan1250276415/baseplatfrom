FROM gradle:8.10.0-jdk21-alpine AS builder
ENV GRADLE_USER_HOME=/cache
ENV WORKDIR=/usr/src/app
WORKDIR $WORKDIR
RUN --mount=type=bind,target=.,rw \
    --mount=type=cache,target=$GRADLE_USER_HOME \
    ./gradlew -i jooqCodegen &&  \
    ./gradlew -i bootJar --stacktrace && \
    mv $WORKDIR/build/libs/baseplatfrom.jar /baseplatfrom.jar
RUN echo "Cache stage completed" && ls -la /baseplatfrom.jar

FROM bellsoft/liberica-openjdk-alpine:21 AS RUNNER
ENV WORKDIR=/usr/src/app
WORKDIR $WORKDIR
EXPOSE 8080
RUN mkdir -p /var/log
RUN apk add -q tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
COPY --from=builder /baseplatfrom.jar ./baseplatfrom.jar
CMD java --add-opens java.base/java.lang=ALL-UNNAMED -jar ./baseplatfrom.jar
