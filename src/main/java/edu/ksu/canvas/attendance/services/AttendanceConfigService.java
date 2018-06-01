package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.ConfigItem;
import edu.ksu.canvas.attendance.repository.ConfigRepository;
import edu.ksu.lti.launch.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConfigService implements ConfigService {

    @Autowired
    private ConfigRepository configRepository;

    @Override
    public String getConfigValue(String property) {
        //Because these are stored as "COMMON" attributes, we need to pull them slightly differently
        if ("canvas_url".equals(property) || "canvas_url_2".equals(property)){
            return configRepository.findByLtiApplicationAndKey("COMMON", property).getValue();
        } else if ("oauth_client_id".equals(property) || "oauth_client_secret".equals(property)) {
            return configRepository.findByLtiApplicationAndKey("Attendance", property).getValue();
        }
        final ConfigItem configItem = configRepository.findByLtiApplicationAndKey("Attendance", property);
        return configItem.getValue();
    }

}