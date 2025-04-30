package org.example.Config;

import org.example.Enum.UserTypeEnum;
import org.example.Model.UserType;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToUserTypeConverter implements Converter<String, UserType> {
    @Override
    public UserType convert(String source) {
        UserTypeEnum userTypeEnum = UserTypeEnum.valueOf(source.trim().toUpperCase());
        return new UserType(userTypeEnum);
    }
}