/*
 * Copyright (c) 2002-2017 "Neo Technology,"
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
package org.neo4j.driver.v1.stress;

import java.util.concurrent.CompletionStage;

import org.neo4j.driver.internal.util.Futures;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResultCursor;
import org.neo4j.driver.v1.Transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.neo4j.driver.internal.util.Matchers.arithmeticError;

public class AsyncFailingQueryInTx<C extends AbstractContext> extends AbstractAsyncQuery<C>
{
    public AsyncFailingQueryInTx( Driver driver )
    {
        super( driver, false );
    }

    @Override
    public CompletionStage<Void> execute( C context )
    {
        Session session = newSession( AccessMode.READ, context );

        return session.beginTransactionAsync()
                .thenCompose( tx -> tx.runAsync( "UNWIND [10, 5, 0] AS x RETURN 10 / x" )
                        .thenCompose( StatementResultCursor::listAsync )
                        .handle( ( records, error ) ->
                        {
                            assertNull( records );
                            Throwable cause = Futures.completionErrorCause( error );
                            assertThat( cause, is( arithmeticError() ) );

                            return tx;
                        } ) )
                .thenCompose( Transaction::rollbackAsync )
                .whenComplete( ( ignore, error ) -> session.closeAsync() );
    }
}
