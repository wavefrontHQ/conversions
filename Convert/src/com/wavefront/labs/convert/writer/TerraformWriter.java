package com.wavefront.labs.convert.writer;

import com.wavefront.labs.convert.Writer;
import com.wavefront.rest.models.Alert;
import com.wavefront.rest.models.Dashboard;
import com.wavefront.rest.models.MaintenanceWindow;
import com.wavefront.rest.models.UserToCreate;

import java.util.Properties;

public class TerraformWriter implements Writer {

    @Override
    public void init(Properties properties) {

    }

    @Override
    public void writeDashboard(Dashboard dashboard) {

    }

    @Override
    public void writeAlert(Alert alert) {

    }

    @Override
    public void writeMaintenanceWindow(MaintenanceWindow maintenanceWindow) {

    }

    @Override
    public void writeUser(UserToCreate user) {

    }
}
