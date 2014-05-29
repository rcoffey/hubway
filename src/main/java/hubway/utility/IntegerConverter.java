package hubway.utility;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class IntegerConverter implements TypeConverter<String, Integer> {

	public IntegerConverter() {
		// TODO Auto-generated constructor stub
	}

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return targetClass.equals(Integer.class)
				&& sourceClass.equals(String.class);
	}

	public Integer convert(String source, JavaType targetType,
			ConversionContext context, TypeConversionHints hints) {
		if (source.length() > 0) {
			return Integer.parseInt(source);
		}
		return null;
	}

}
