import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.action.setFieldActionTemplate;
import com.linewx.law.parser.action.setFieldWithRegActionTemplate;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 12/10/16.
 */
public class firstCivilAdjugementTest {
    @Test
    public void AccuserTest() {
        String accuserPattern = "^(?:原告|公诉机关|起诉人)(?:：)?([\u4e00-\u9fa5]*)";

        List<Pair<String, String>> testSuite = new ArrayList<>();
        testSuite.add(new ImmutablePair<>("原告匡某，女，1976年1月14日生，汉族，湖南省常宁市人，小学文化，农民。", "匡某"));
        verifyField(accuserPattern, testSuite);
    }


    private void verifyField(String reg, List<Pair<String, String>> matchString) {

        for (Pair<String, String> onePair: matchString) {
            ParseContext context = new ParseContext();
            context.setCurrentStatement(onePair.getLeft());
            List<JsonElement> parameters = new ArrayList<>();
            parameters.add(new JsonPrimitive("test"));
            parameters.add(new JsonPrimitive("cur"));
            //set pattern
            parameters.add(new JsonPrimitive(reg));
            setFieldWithRegActionTemplate setFieldWithRegActionTemplate = new setFieldWithRegActionTemplate();
            setFieldWithRegActionTemplate.execute(context, parameters);
            Assert.assertEquals(context.getResults().get("test").get(0), onePair.getRight());
        }

    }


}
