/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.datamodel.model;

/**
 * Interface representing the client's allowed scope
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface ClientScope {

    /****
     * Get the scope
     * 
     * @return the scope
     */
    String getScope();

    /****
     * Get the description
     * 
     * @return the description
     */
    String getDescription();
}
