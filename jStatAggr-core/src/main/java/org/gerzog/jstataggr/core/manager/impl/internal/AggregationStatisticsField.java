package org.gerzog.jstataggr.core.manager.impl.internal;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.core.templates.TemplateHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;
import org.gerzog.jstataggr.core.utils.InitializerUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AggregationStatisticsField extends AbstractStatisticsField {

	private final Class<?> updaterType;

	private final AggregationType aggregationType;

	protected AggregationStatisticsField(final String fieldName, final Class<?> dataType, final Class<?> updaterType, final AggregationType aggregationType) {
		super(fieldName, dataType);

		this.updaterType = updaterType;
		this.aggregationType = aggregationType;
	}

	public AggregationStatisticsField(final String fieldName, final Class<?> dataType, final AggregationType aggregationType) {
		this(fieldName, dataType, dataType, aggregationType);
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		super.generate(clazz);

		generateUpdater(clazz);
	}

	protected void generateUpdater(final CtClass clazz) throws Exception {
		final CtMethod method = CtMethod.make(getUpdaterText(), clazz);

		clazz.addMethod(method);
	}

	protected String getUpdaterText() {
		return TemplateHelper.simpleUpdater(generateFieldName(), updaterType, aggregationType);
	}

	@Override
	protected String generateFieldName() {
		return FieldUtils.getAggregationFieldName(generateFieldName(), aggregationType);
	}

	@Override
	protected void addField(final CtClass clazz, final CtField field) throws Exception {
		clazz.addField(field, InitializerUtils.getInitializer(getDataType(), aggregationType));
	}

	@Override
	protected String getAccessMethodName() {
		return FieldUtils.getUpdaterName(generateFieldName(), aggregationType);
	}

	@Override
	protected Class<?> getAccessMethodType() {
		return updaterType;
	}
}
