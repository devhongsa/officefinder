package com.dokkebi.officefinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Slf4j
@Profile("test")
@Configuration
public class LocalRedisConfig {

  @Value("${spring.redis.port}")
  private int redisPort;

  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() throws IOException {
    int port = isRedisRunning() ? findAvailablePort() : redisPort;
    redisServer = RedisServer.builder()
        .port(port)
        .setting("maxmemory 128M")
        .build();

    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }

  // 임베디드 레디스가 현재 실행 중인지(또는 6379포트가 이미 사용 중인지 확인)
  private boolean isRedisRunning() throws IOException {
    return isRunning(executeGrepProcessCommand(redisPort));
  }

  // 임베디드 레디스가 이미 실행되어 포트가 사용 불가능하다면 사용 가능한 새로운 포트를 찾는다.
  private int findAvailablePort() throws IOException {
    for (int port = 10000; port <= 65535; port++) {
      Process process = executeGrepProcessCommand(port);

      if (!isRunning(process)) {
        return port;
      }
    }

    throw new IllegalArgumentException("Not Found Available Port port 10000 ~ 65535");
  }

  // 해당 포트를 사용하는 프로세스를 확인하는 shell script 실행
  private Process executeGrepProcessCommand(int port) throws IOException {
    String currentOS = System.getProperty("os.name").toLowerCase();

    // 현재 실행 OS = Windows 라면
    if (currentOS.contains("win")) {
      log.info("current OS is {}, port is {}", currentOS, port);

      String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
      String[] shell = {"cmd.exe", "/y", "/c", command};

      return Runtime.getRuntime().exec(shell);
    }

    String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
    String[] shell = {"/bin/sh", "-c", command};

    return Runtime.getRuntime().exec(shell);
  }

  // 해당 process가 실행 중인지 확인
  private boolean isRunning(Process process) {
    String line;
    StringBuilder pidInfo = new StringBuilder();

    try (BufferedReader input = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {

      while ((line = input.readLine()) != null) {
        pidInfo.append(line);
      }
    } catch (IOException e) {
    }

    return StringUtils.hasText(pidInfo.toString());
  }
}
