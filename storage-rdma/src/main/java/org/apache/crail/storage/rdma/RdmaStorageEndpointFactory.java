/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.crail.storage.rdma;

import java.io.IOException;

import com.ibm.disni.*;
import com.ibm.disni.verbs.*;

public class RdmaStorageEndpointFactory implements RdmaEndpointFactory<RdmaStorageServerEndpoint> {
	private RdmaStorageServer closer;
	private RdmaPassiveEndpointGroup<RdmaStorageServerEndpoint> group;
	
	public RdmaStorageEndpointFactory(RdmaPassiveEndpointGroup<RdmaStorageServerEndpoint> group, RdmaStorageServer closer){
		this.group = group;
		this.closer = closer;
	}
	
	@Override
	public RdmaStorageServerEndpoint createEndpoint(RdmaCmId id, boolean serverSide) throws IOException {
		return new RdmaStorageServerEndpoint(group, id, closer, serverSide);
	}
}
