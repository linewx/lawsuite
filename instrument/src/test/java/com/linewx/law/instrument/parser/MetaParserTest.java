package com.linewx.law.instrument.parser;

import com.google.gson.Gson;
import com.linewx.law.instrument.json.InstrumentRuleConverter;
import com.linewx.law.instrument.json.InstrumentRuleJson;
import com.linewx.law.instrument.meta.MetaInstrumentParser;
import com.linewx.law.instrument.meta.model.InstrumentMetadata;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.parser.json.RuleJson;
import com.sun.media.jfxmedia.MetadataParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luganlin on 12/10/16.
 */
public class MetaParserTest {
    @Test
    public void testParser () throws Exception{
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rule/metaRule.json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
        Gson gson = new Gson();
        InstrumentRuleJson instrumentRuleJson = gson.fromJson(bufferedReader, InstrumentRuleJson.class);
        RuleJson ruleJson = InstrumentRuleConverter.convertInstrumentRuleToParserRule(instrumentRuleJson);
        InstrumentMetadata metadata = MetaInstrumentParser.parse(readFileContent("fixtures/testmetafile.txt"), ruleJson, true);

    }

    private List<String> readFileContent(String path) throws Exception{
        List<String> fileContent = new ArrayList<>();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
        String currentLine;
        while( (currentLine = bufferedReader.readLine()) != null) {
            fileContent.add(currentLine);
        }
        return fileContent;
    }

}
