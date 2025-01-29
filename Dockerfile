ARG JAVA_VERSION=23

# ---------------------------------------------------------------------------------------
# S t a g e :   b u i l d e r
#
#   Azul Zulu: JDK, nicht JRE (s.u.)
#   Ubuntu "Jammy Jellyfish" 22.04 LTS (Long Term Support) https://ubuntu.com/about/release-cycle https://wiki.ubuntu.com/Releases
#   JAR bauen mit eigenem Code und Dependencies z.B. Spring, Jackson
# ---------------------------------------------------------------------------------------
FROM azul/zulu-openjdk:${JAVA_VERSION} AS builder

# "working directory" fuer die Docker-Kommandos RUN, ENTRYPOINT, CMD, COPY und ADD
WORKDIR /source

# ADD hat mehr Funktionalitaet als COPY, z.B. auch Download von externen Dateien

# Gradle:
COPY build.gradle.kts gradle.properties gradlew settings.gradle.kts ./
COPY gradle ./gradle

# Maven:
#COPY pom.xml mvnw ./
#COPY .mvn ./.mvn

COPY src ./src

# JAR-Datei mit den Schichten ("layers") erstellen und aufbereiten bzw. entpacken
RUN <<EOF
# https://unix.stackexchange.com/questions/217369/clear-apt-get-list
set -eux

# Gradle:
./gradlew --no-configuration-cache --no-daemon --no-watch-fs bootJar
java -Djarmode=layertools -jar ./build/libs/customer-27.01.2025.jar extract

# Maven:
#./mvnw package spring-boot:repackage -Dmaven.test.skip=true -Dspring-boot.build-image.skip=true
#java -Djarmode=layertools -jar ./target/customer-27.01.2025.jar extract
EOF

# ---------------------------------------------------------------------------------------
# S t a g e   f i n a l
#
#   JRE statt JDK
#   Dependencies: z.B. Spring, Jackson
#   Loader fuer Spring Boot
#   Eigener uebersetzter Code
# ---------------------------------------------------------------------------------------

FROM azul/zulu-openjdk:${JAVA_VERSION}-jre AS final

# Anzeige bei "docker inspect ..."
# https://specs.opencontainers.org/image-spec/annotations
# https://spdx.org/licenses
# https://snyk.io/de/blog/how-and-when-to-use-docker-labels-oci-container-annotations
# MAINTAINER ist deprecated https://docs.docker.com/engine/reference/builder/#maintainer-deprecated
LABEL org.opencontainers.image.title="Customer Microservice" \
      org.opencontainers.image.description="Spring Boot Microservice for Customer Management, based on Azul Zulu and Ubuntu Jammy (22.04 LTS)" \
      org.opencontainers.image.version="2025.01.29" \
      org.opencontainers.image.revision="LATEST_VERSION" \
      org.opencontainers.image.licenses="GPL-3.0-or-later" \
      org.opencontainers.image.vendor="GentleCorp Systems" \
      org.opencontainers.image.authors="Caleb Script <caleb-script@gentlecorp-systems.de>" \
      org.opencontainers.image.source="https://github.com/GentleCorp-Systems/customer-service" \
      org.opencontainers.image.url="https://gentlecorp-systems.com/customer-service" \
      org.opencontainers.image.documentation="https://docs.gentlecorp-systems.com/customer-service" \
      org.opencontainers.image.base.name="azul/zulu-openjdk:${JAVA_VERSION}-jre" \
      org.opencontainers.image.created="$(date -u +'%Y-%m-%dT%H:%M:%SZ')" \
      org.opencontainers.image.ref.name="gentlecorp-systems/customer-service:2025.01.29" \
      org.opencontainers.image.version.schema="Semantic Versioning (https://semver.org)" \
      org.opencontainers.image.maintainers="Caleb Script <caleb-script@gentlecorp-systems.de>"

# gleiches Basis-Verzeichnis wie bei "Cloud Native Buildpacks": Mounting von application.yml, private-key.pem, certificate.crt
WORKDIR /workspace

# "here document" wie in einem Shellscipt
RUN <<EOF
set -eux
apt-get update
apt-get upgrade --yes
# https://unix.stackexchange.com/questions/217369/clear-apt-get-list
apt-get autoremove -y
apt-get clean -y
rm -rf /var/lib/apt/lists/*
# https://manpages.debian.org/bookworm/passwd/groupadd.html
# https://manpages.debian.org/bookworm/adduser/addgroup.html
groupadd --gid 1000 app
# https://manpages.debian.org/bookworm/passwd/useradd.html
# https://manpages.debian.org/bookworm/adduser/adduser.html
useradd --uid 1000 --gid app --no-create-home app
chown -R app:app /workspace
EOF

# ADD hat mehr Funktionalitaet als COPY, z.B. auch Download von externen Dateien
# ggf. auch /source/snapshot-dependencies/
COPY --from=builder --chown=app:app /source/dependencies/ /source/spring-boot-loader/ /source/application/ ./

USER app
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=1 CMD wget -qO- --no-check-certificate https://localhost:8080/actuator/health/ | grep UP || exit 1

# Bei CMD statt ENTRYPOINT kann das Kommando bei "docker run ..." ueberschrieben werden
ENTRYPOINT ["java", "--enable-preview", "org.springframework.boot.loader.launch.JarLauncher"]
