package com.may.app.feed;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FeedResourceTypeConverter implements AttributeConverter<FeedResourceType, String> {


	@Override
	public String convertToDatabaseColumn(FeedResourceType attribute) {
		if (attribute == null) return null;
		return attribute.toString();
	}

	@Override
	public FeedResourceType convertToEntityAttribute(String dbData) {
		if (dbData == null) return null;
		return FeedResourceType.valueOf(dbData);
	}
	
}
