/**
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver;

import java.util.Map;

/**
 * Common interface for components that can execute Neo4j statements.
 *
 * @see org.neo4j.driver.Session
 * @see org.neo4j.driver.Transaction
 */
public interface StatementRunner
{
    /**
     * Run a statement and return a result stream.
     * <p>
     * This method takes a set of parameters that will be injected into the
     * statement by Neo4j. Using parameters is highly encouraged, it helps avoid
     * dangerous cypher injection attacks and improves database performance as
     * Neo4j can re-use query plans more often.
     * <p>
     * <h2>Example</h2>
     * <pre>
     * {@code
     * Result res = session.run( "MATCH (n) WHERE n.name = {myNameParam} RETURN (n)",
     *              Neo4j.parameters( "myNameParam", "Bob" ) );
     * }
     * </pre>
     *
     * @param statement a Neo4j statement
     * @param parameters input data for the statement, see {@link org.neo4j.Neo4j#parameters(Object[])}
     * @return a stream of result values and associated metadata
     */
    Result run( String statement, Map<String,Value> parameters );

    /**
     * Run a statement and return a result stream.
     *
     * @param statement a Neo4j statement
     * @return a stream of result values and associated metadata
     */
    Result run( String statement );

}
