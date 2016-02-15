/**
 * Copyright (c) 2002-2016 "Neo Technology,"
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
package org.neo4j.driver.v1;

import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;

import java.util.List;


/**
 * The result of running a statement, a stream of records represented as a cursor.
 *
 * The result cursor can be used to iterate over all the records in the stream and provide access
 * to their content.
 *
 * Results are valid until the next statement is run or until the end of the current transaction,
 * whichever comes first.
 *
 * Initially, before {@link #next()} has been called at least once, all field values are null.
 *
 * To keep a result around while further statements are run, or to use a result outside the scope
 * of the current transaction, see {@link #list()}.
 */
public interface ResultCursor extends RecordAccessor, Resource
{
    /**
     * @return an immutable copy of the currently viewed record
     * @throws NoSuchRecordException if no calls has been made to {@link #next()}, {@link #first()}, nor {@link #skip(long)}
     */
    @Override
    Record record() throws NoSuchRecordException;

    /**
     * Retrieve the zero based position of the cursor in the stream of records.
     *
     * Initially, before {@link #next()} has been called at least once, the position is -1.
     *
     * @return the current position of the cursor
     */
    long position();

    /**
     * Test if the cursor is positioned at the last stream record or if the stream is empty.
     *
     * @return <tt>true</tt> if the cursor is at the last record or the stream is empty.
     */
    boolean atEnd();

    /**
     * Move to the next record in the result.
     *
     * @return <tt>true</tt> if there was another record, <tt>false</tt> if the stream is exhausted.
     */
    boolean next();

    /**
     * Advance the cursor as if calling next multiple times.
     *
     * @throws ClientException if records is negative
     * @param records amount of records to be skipped
     * @return the actual number of records successfully skipped
     */
    long skip( long records );

    /**
     * Limit this cursor to return no more than the given number of records after the current record.
     * As soon as the described amount of records have been returned, all further records are discarded.
     * Calling limit again before the described amount of records have been returned, replaces the limit (overwriting the previous limit).
     *
     * @throws ClientException if records is negative
     * @param records the maximum number of records to return from future calls to {@link #next()}
     * @return the actual position of the last record to be returned
     */
    long limit( long records );

    /**
     * Return the first record in the stream. Fail with an exception if the stream is empty
     * or if this cursor has already been used to move past the first record.
     *
     * @return the first record in the stream
     * @throws NoSuchRecordException if there is no first record or the cursor has been used already
     *
     */
    Record first() throws NoSuchRecordException;

    /**
     * Return a field value from the first record in the stream. Fail with an exception if the stream is empty
     * or if this cursor has already been used to move past the first record.
     *
     * @param fieldName the name of the field to return from the first record in the stream
     * @return the specified field from the first record in the stream
     * @throws NoSuchRecordException if there is no first record or the cursor has been used already
     *
     */
    Value first( String fieldName ) throws NoSuchRecordException;

    /**
     * Return a field value from the first record in the stream. Fail with an exception if the stream is empty
     * or if this cursor has already been used to move past the first record.
     *
     * @param index the index of the field to return from the first record in the stream
     * @return the specified field from the first record in the stream
     * @throws NoSuchRecordException if there is no first record or the cursor has been used already
     *
     */
    Value first( int index ) throws NoSuchRecordException;

    /**
     * Move to the first record and return an immutable copy of it, failing if there is not exactly
     * one record in the stream, or if this cursor has already been used to move past the first record.
     *
     * @return the first and only record in the stream
     * @throws NoSuchRecordException if there is not exactly one record in the stream, or if the cursor has been used already
     */
    Record single() throws NoSuchRecordException;

    /**
     * Move to the first record and return a field value from it, failing if there is not exactly
     * one record in the stream, or if this cursor has already been used to move past the first record.
     *
     * @param fieldName the name of the field to return from the first and only record in the stream
     * @return the value of the specified field of the first and only record in the stream
     * @throws NoSuchRecordException if there is not exactly one record in the stream, or if the cursor has been used already
     */
    Value single( String fieldName ) throws NoSuchRecordException;

    /**
     * Move to the first record and return a field value from it, failing if there is not exactly
     * one record in the stream, or if this cursor has already been used to move past the first record.
     *
     * @param index the index of the field to return from the first and only record in the stream
     * @return the value of the specified field of the first and only record in the stream
     * @throws NoSuchRecordException if there is not exactly one record in the stream, or if the cursor has been used already
     */
    Value single( int index ) throws NoSuchRecordException;

    /**
     * Investigate the next upcoming record without changing the position of this cursor.
     *
     * @return an immutable copy of the next record, or null if there is no next record
     */
    Record peek();

    /**
     * Retrieve and store the entire result stream.
     * This can be used if you want to iterate over the stream multiple times or to store the
     * whole result for later use.
     *
     * Calling this method exhausts the result cursor and moves it to the last record.
     *
     * @throws ClientException if the cursor can't be positioned at the first record
     * @return list of all immutable records
     */
    List<Record> list();

    /**
     * Retrieve and store a projection of the entire result stream.
     * This can be used if you want to iterate over the stream multiple times or to store the
     * whole result for later use.
     *
     * Calling this method exhausts the result cursor and moves it to the last record.
     *
     * @throws ClientException if the cursor can't be positioned at the first record
     * @param mapFunction a function to map from Value to T. See {@link Values} for some predefined functions, such
     * as {@link Values#valueAsBoolean()}, {@link Values#valueAsList(Function)}.
     * @param <T> the type of result list elements
     * @return list of all mapped immutable records
     */
    <T> List<T> list( Function<RecordAccessor, T> mapFunction );

    /**
     * Summarize the result.
     *
     * Calling this method exhausts the result cursor and moves it to the last record.
     *
     * <pre class="doctest:ResultDocIT#summarizeUsage">
     * {@code
     * ResultSummary summary = session.run( "PROFILE MATCH (n:User {id: 12345}) RETURN n" ).summarize();
     * }
     * </pre>
     *
     * @return a summary for the whole query
     */
    ResultSummary summarize();
}
