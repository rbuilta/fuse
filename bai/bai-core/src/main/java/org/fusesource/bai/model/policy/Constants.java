/*
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.bai.model.policy;

/**
 * Constants that model Actions, Filtering Elements, Event Types, etc.
 * @author Raul Kripalani
 *
 */
public class Constants {

	public enum ActionType {
		INCLUDE,
		EXCLUDE
	}
	
	public enum FilterElement {
		CONTEXT,
		EVENT,
		EXCHANGE,
		BUNDLE,
		ENDPOINT
	}
	
	public enum EventType {
		CREATED,
		COMPLETED,
		SENDING,
		SENT,
		FAILURE,
		FAILURE_HANDLED,
		REDELIVERY		
	}
	
	public enum FilterMethod {
		EXPRESSION,
		ENUM_VALUE_ONE,
		ENUM_VALUE_MULTIPLE
	}
	
}