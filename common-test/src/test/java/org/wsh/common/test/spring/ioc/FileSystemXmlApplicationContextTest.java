package org.wsh.common.test.spring.ioc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class FileSystemXmlApplicationContextTest extends org.springframework.context.support.FileSystemXmlApplicationContext {

    private final Logger log = LoggerFactory.getLogger(FileSystemXmlApplicationContextTest.class);

    @Test
    public void test() {

        String location = "beans.xml";
        setConfigLocation(location);
        Resource resource = getResourceByPath(location);
        log.info("fileName:" + resource.getFilename());
    }

}

