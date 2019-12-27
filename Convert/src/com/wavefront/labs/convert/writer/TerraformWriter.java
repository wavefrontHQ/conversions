package com.wavefront.labs.convert.writer;

import com.wavefront.labs.convert.Utils;
import com.wavefront.labs.convert.Writer;
import com.wavefront.rest.models.Alert;
import com.wavefront.rest.models.Dashboard;
import com.wavefront.rest.models.MaintenanceWindow;
import com.wavefront.rest.models.UserToCreate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TerraformWriter implements Writer {

    private static final Logger logger = LogManager.getLogger(FolderWriter.class);

    private String baseFolder;

    @Override
    public void init(Properties properties) {
        baseFolder = properties.getProperty("convert.writer.folder", "converted/");
        if (!baseFolder.endsWith(File.separator)) {
            baseFolder += File.separator;
        }
    }

    @Override
    public void writeDashboard(Dashboard dashboard) {

    }

    @Override
    public void writeAlert(Alert alert) {
        try {
            String fileName = Utils.sluggify(alert.getName()) + ".tf";

            File file = new File(baseFolder + "alerts");
            file.mkdirs();

            Files.write(Paths.get(file.getAbsolutePath() + File.separator + fileName), getTerraformCode(alert).getBytes());
        } catch (IOException e) {
            logger.error("Error writing alert: " + alert.getName(), e);
        }
    }

    @Override
    public void writeMaintenanceWindow(MaintenanceWindow maintenanceWindow) {

    }

    @Override
    public void writeUser(UserToCreate user) {

    }

    private String getTerraformCode(Alert alert) {
        StringBuffer sb = new StringBuffer("");

        terraformAddProvider(sb);
        terraformAddResource(sb, alert);

        return sb.toString();
    }

    private void terraformAddProvider(StringBuffer sb) {
        sb.append(
            "provider \"wavefront\" {\n" +
            "  address = \"cloudhealth.wavefront.com\"\n" +
            "  token = \"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\"\n" +
            "}\n"
        );
    }

    private void terraformAddResource(StringBuffer sb, Alert alert) {
        sb.append(
            "resource \"wavefront_alert\" \"test_alert_1\" {\n" +
            "  name = \"" + alert.getName() + "\"\n" +
            "  target = \"xxxxxx@cloudhealthtech.com\"\n" +
            "  condition = \"" + alert.getCondition() + "\"\n" +
            "  display_expression = \"" + alert.getCondition() + "\"\n" +
            "  minutes = 5\n" +
            "  resolve_after_minutes = 5\n" +
            "  severity = \"WARN\"\n" +
            "  tags = [\n" +
            "    \"terraform\",\n" +
            "    \"datadog\"\n" +
            "  ]\n" +
            "}"
        );
    }
}
