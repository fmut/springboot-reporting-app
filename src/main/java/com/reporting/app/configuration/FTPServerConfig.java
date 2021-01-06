package com.reporting.app.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FTPServerConfig {

    @Value("${sftp.server.url}")
    private String sftpUrl;

    @Value("${sftp.server.user}")
    private String sftpUser;

    @Value("${sftp.server.password}")
    private String sftpPassword;
}
