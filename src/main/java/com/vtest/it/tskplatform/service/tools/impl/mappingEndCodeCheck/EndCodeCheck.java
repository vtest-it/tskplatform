package com.vtest.it.tskplatform.service.tools.impl.mappingEndCodeCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class EndCodeCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndCodeCheck.class);

    public boolean check(File mapping) {
        try {
            FileInputStream fileInputStream = new FileInputStream(mapping);
            byte[] bs = new byte[(int) mapping.length()];
            fileInputStream.read(bs);
            fileInputStream.close();

            boolean flag = true;
            for (int i = bs.length - 1; i > bs.length - 8; i--) {
                if (bs[i] != 0) {
                    LOGGER.error(mapping.getName() + " & end code check result: " + false);
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                for (int i = bs.length - 1; i > bs.length - 33; i--) {
                    if (bs[i] != 32) {
                        LOGGER.error(mapping.getName() + " & end code check result: " + false);
                        return false;
                    }
                }
            }
            LOGGER.error(mapping.getName() + " & end code check result: " + true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
