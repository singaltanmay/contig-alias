package com.ebivariation.contigalias.dus;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FTPBrowser {

    private static final int DEFAULT_FTP_PORT = 21;

    private final Logger logger = LoggerFactory.getLogger(FTPBrowser.class);

    private final FTPClient ftp = new FTPClient();

    public void connect(String address) throws IOException {
        connect(address, DEFAULT_FTP_PORT);
    }

    public void connect(String address, int port) throws IOException {

        try {
            ftp.connect(address, port);
            // After connection attempt, you should check the reply code to verify
            // success.
            int reply = ftp.getReplyCode();
            logger.debug("Connected to {} with reply code {}.", address, reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new RuntimeException("FTP refused connection");
            }

            // Login as anonymous user
            ftp.enterLocalPassiveMode();
            boolean login = ftp.login("anonymous", "anonymous");
            logger.debug("Login {}.", (login ? "successful" : "unsuccessful"));
            if (!login) {
                throw new RuntimeException("FTP refused login");
            }

            String status = ftp.getStatus();
            logger.debug("FTP connection status: {}", status);
            logger.info("Connected successfully to {}", address);
        } catch (Exception e) {
            logger.error("Could not connect to FTP server '{}'. FTP status was: {}. Reply code: {}. Reply string: {}",
                    address, ftp.getStatus(), ftp.getReply(), ftp.getReplyString());
            ftp.disconnect();
            throw e;
        }
    }

    public void disconnect() throws IOException {
        if (ftp.isConnected()) {
            ftp.logout();
            ftp.disconnect();
        }
    }

    public FTPFile[] listFiles() throws IOException {
        return ftp.listFiles();
    }

    public FTPFile[] listDirectories() throws IOException {
        return ftp.listDirectories();
    }

    public FTPFile[] listDirectories(String path) throws IOException {
        return ftp.listDirectories(path);
    }

    public void navigateToDirectory(String directory) throws IOException {
        if (!ftp.changeWorkingDirectory(directory)) {
            throw new RuntimeException("Unable to change to directory '" + directory + "'");
        }
    }

}