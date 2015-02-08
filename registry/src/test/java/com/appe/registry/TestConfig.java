/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.registry;

import com.appe.registry.AppeConfig.Bundle;
import com.appe.registry.AppeConfig.Entry;
/**
 * A test bundle using system by default, just annotate how to get it out and you got it.
 * 
 * @author ho
 *
 */
@Bundle
public interface TestConfig {
	/**
	 * 
	 * @return
	 */
	@Entry(name="test.number_of_cars", defaultValue="1")
	public int numberOfCars();
	
	/**
	 * 
	 * @return
	 */
	@Entry(name="test.message", defaultValue="lalala")
	public String message();
	
	/**
	 * 
	 * @return
	 */
	@Entry(name="test.notAvailable")
	public String notAvailable();
}
