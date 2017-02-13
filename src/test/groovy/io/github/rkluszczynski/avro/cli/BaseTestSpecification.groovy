package io.github.rkluszczynski.avro.cli

import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Specification

abstract class BaseTestSpecification extends Specification {
    @Rule
    OutputCapture capture = new OutputCapture()

    protected trimmedOutput() {
        capture.toString().trim()
    }

    protected compareFilesContent(filePath1, filePath2) {
        def contentText1 = new File(filePath1).text
        def contentText2 = new File(filePath2).text

        contentText1 == contentText2
    }
}