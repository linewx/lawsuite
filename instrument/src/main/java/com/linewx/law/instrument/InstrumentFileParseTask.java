package com.linewx.law.instrument;

import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.json.RuleJson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by luganlin on 11/26/16.
 */
public class InstrumentFileParseTask implements Callable<Boolean>{
    private RuleJson ruleJson;
    private File file;
    private InstrumentService instrumentService;

    public InstrumentFileParseTask(RuleJson ruleJson, File file, InstrumentService instrumentService) {
        this.ruleJson = ruleJson;
        this.file = file;
        this.instrumentService = instrumentService;
    }

    @Override
    public Boolean call() throws Exception {
        ParseContext context = new ParseContext();
        List<String> statements = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(file, "GBK");
            Element element = doc.getElementById("DivContent");
            Elements elements = element.children();


            context.addResult("filename", file.getName());
            for (Element oneElement : elements) {
                statements.add(oneElement.ownText());
            }

            InstrumentParser parser = new InstrumentParser(ruleJson, InstrumentTypeEnum.CIVIL_JUDGMENT);
            Instrument instrument = parser.parse(statements);
            instrumentService.save(instrument);

            //instrumentService.save(instrument);

        } catch (InstrumentParserException e) {
            if (!e.getErrorCode().equals(InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE)) {
                if (!e.getMessage().equals("不明案由")) {

                }
            }else {
                //
                //total.incrementAndGet();
                //unsupported.incrementAndGet();
            }

        } catch (Exception e) {
            //e.printStackTrace();
            //total.incrementAndGet();
            //error.incrementAndGet();
        }
        return true;
    }
}
