FROM openjdk:23-slim
LABEL authors="remremegg"
COPY ./out/production/AquaQuartz/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "AquaQuartz"]
EXPOSE 443