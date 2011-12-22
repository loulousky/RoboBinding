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
package org.robobinding.viewattribute.adapterview;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robobinding.viewattribute.AbstractPropertyViewAttribute;
import org.robobinding.viewattribute.AbstractSingleTypeTwoWayPropertyAttributeTest;
import org.robobinding.viewattribute.MockArrayAdapter;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(RobolectricTestRunner.class)
public class SelectedItemPositionAttributeTest extends AbstractSingleTypeTwoWayPropertyAttributeTest<Integer>
{
	private ListView adapterView;
	private ArrayAdapter<String> arrayAdapter;
	
	@Before
	public void setUp()
	{
		adapterView = new ListView(null);
		arrayAdapter = new MockArrayAdapter();
		adapterView.setAdapter(arrayAdapter);
	}
	
	@Test
	@Ignore
	//TODO Enable this test when the appropriate support has been added to Robolectric
	public void whenAllItemsAreRemovedFromAdapter_ThenSelectedItemPositionShouldEqualInvalidPosition()
	{
		createAttributeWith2WayBinding();
		
		arrayAdapter.clear();
		arrayAdapter.notifyDataSetChanged();
		
		assertThat(valueModel.getValue(), is(AdapterView.INVALID_POSITION));
	}
	
	@Override
	protected void populateBindingExpectations(BindingSamples<Integer> bindingSamples)
	{
		bindingSamples.add(1,0,5);		
	}

	@Override
	protected void updateViewState(Integer newValue)
	{
		adapterView.setSelection(newValue);
	}

	@Override
	protected Integer getViewState()
	{
		return adapterView.getSelectedItemPosition();
	}

	@Override
	protected AbstractPropertyViewAttribute<Integer> newAttributeInstance(String bindingAttributeValue)
	{
		return new SelectedItemPositionAttribute(adapterView, bindingAttributeValue, true);
	}
}