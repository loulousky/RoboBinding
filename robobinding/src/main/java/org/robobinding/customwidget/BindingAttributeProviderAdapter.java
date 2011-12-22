/**
 * Copyright 2011 Cheng Wei, Robert Taylor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.robobinding.customwidget;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.robobinding.binder.BindingAttributeResolver;
import org.robobinding.internal.com_google_common.collect.Lists;
import org.robobinding.internal.com_google_common.collect.Maps;
import org.robobinding.internal.org_apache_commons_lang3.StringUtils;
import org.robobinding.viewattribute.BindingAttributeProvider;
import org.robobinding.viewattribute.ViewAttribute;

import android.view.View;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
class BindingAttributeProviderAdapter implements BindingAttributeProvider<View>
{
	private View customWidget;
	public BindingAttributeProviderAdapter(View customWidget)
	{
		this.customWidget = customWidget;
	}
	@Override
	public void resolveSupportedBindingAttributes(View view, BindingAttributeResolver bindingAttributeResolver, boolean preInitializeViews)
	{
		if(customWidget instanceof BindableView)
		{
			BindableView bindableView = (BindableView)customWidget;
			String[] supportedAttributes = bindableView.getSupportedAttributes();
			for(String supportedAttribute : supportedAttributes)
			{
				if(bindingAttributeResolver.hasAttribute(supportedAttribute))
				{
					ViewAttribute viewAttribute = bindableView.createViewAttribute(
							new Attribute(supportedAttribute, bindingAttributeResolver.findAttributeValue(supportedAttribute)));
					bindingAttributeResolver.resolveAttribute(supportedAttribute, viewAttribute);
				}
			}
		}
		if(customWidget instanceof BindableViewWithGroupedAttributes)
		{
			BindableViewWithGroupedAttributes bindableViewWithGroupedAttributes = (BindableViewWithGroupedAttributes)customWidget;
			GroupedAttribute[] groupedAttributeSpecs = bindableViewWithGroupedAttributes.getSupportedGroupedAttributes();
			for(GroupedAttribute groupedAttributeSpec : groupedAttributeSpecs)
			{
				List<String> attributes = Lists.newArrayList(groupedAttributeSpec.getCompulsoryAttributes());
				attributes.addAll(groupedAttributeSpec.getOptionalAttributes());
				if(!bindingAttributeResolver.hasOneOfAttributes(attributes.toArray(new String[0])))
				{
					continue;
				}
				Map<String, Attribute> groupedAttributes = Maps.newHashMap();
				List<String> missingCompulsoryAttributes = Lists.newArrayList();
				Collection<String> compulsoryAttributes = groupedAttributeSpec.getCompulsoryAttributes();
				for(String compulsoryAttribute : compulsoryAttributes)
				{
					if(bindingAttributeResolver.hasAttribute(compulsoryAttribute))
					{
						Attribute attribute = new Attribute(compulsoryAttribute, bindingAttributeResolver.findAttributeValue(compulsoryAttribute));
						groupedAttributes.put(compulsoryAttribute, attribute);
					}else
					{
						missingCompulsoryAttributes.add(compulsoryAttribute);
					}
				}
				if(!missingCompulsoryAttributes.isEmpty())
				{
					String customWidgetClassName = customWidget.getClass().getName();
					throw new RuntimeException(
							MessageFormat.format("Property ''{0}'' of {1} has following missing compulsory attributes ''{2}''",
									groupedAttributeSpec.getGroupAttributeName(), customWidgetClassName, StringUtils.join(missingCompulsoryAttributes, ", ")));
				}
				
				Collection<String> optionalAttributes = groupedAttributeSpec.getOptionalAttributes();
				for(String optionalAttribute : optionalAttributes)
				{
					if(bindingAttributeResolver.hasAttribute(optionalAttribute))
					{
						Attribute attribute = new Attribute(optionalAttribute, bindingAttributeResolver.findAttributeValue(optionalAttribute));
						groupedAttributes.put(optionalAttribute, attribute);
					}
				}
				
				ViewAttribute viewAttribute = bindableViewWithGroupedAttributes.createGroupedAttribute(groupedAttributes);
				bindingAttributeResolver.resolveAttributes(groupedAttributes.keySet().toArray(new String[0]), viewAttribute);
			}
		}
	}
}