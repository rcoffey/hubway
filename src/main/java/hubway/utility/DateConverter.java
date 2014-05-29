package hubway.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class DateConverter implements TypeConverter<String, Date> {
	private static DateFormat stationDF = new SimpleDateFormat("M/d/yyyy");

	public DateConverter() {
		// TODO Auto-generated constructor stub
	}

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return targetClass.equals(Date.class)
				&& sourceClass.equals(String.class);
	}

	public Date convert(String source, JavaType targetType,
			ConversionContext context, TypeConversionHints hints)
			throws ConversionException {
		if (source.length() > 0) {
			try {
				return stationDF.parse(source.toString());
			} catch (ParseException e) {
				System.out.println("Missing date(s) for " + source);
			}
		}
		return null;
	}

}
