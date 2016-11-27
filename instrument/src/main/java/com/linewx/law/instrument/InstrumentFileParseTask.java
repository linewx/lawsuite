package com.linewx.law.instrument;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.instrument.parser.ParserFactory;
import com.linewx.law.parser.ParseContext;
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
    private File file;
    private InstrumentService instrumentService;
    private AuditService auditService;

    public InstrumentFileParseTask(File file, InstrumentService instrumentService, AuditService auditService) {
        this.file = file;
        this.instrumentService = instrumentService;
        this.auditService = auditService;
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


            InstrumentParser parser = ParserFactory.getFromStatement(statements);
            if (parser == null) {
                throw new InstrumentParserException(InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE);
            }

            Instrument instrument = parser.parse(statements);
            instrumentService.save(instrument);
            auditService.increase();

            //instrumentService.save(instrument);

        } catch (InstrumentParserException e) {

            if (!e.getErrorCode().equals(InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE)) {
               auditService.increaseError();

            }else {
                auditService.increaseUnsupport();
            }

        } catch (Exception e) {
            auditService.increaseError();
        }
        return true;
    }
}
