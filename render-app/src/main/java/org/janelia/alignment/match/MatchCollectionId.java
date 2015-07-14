package org.janelia.alignment.match;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compound identifier for a match collection.
 *
 * @author Eric Trautman
 */
public class MatchCollectionId
        implements Serializable {

    private final String owner;
    private final String name;
    private final String dbCollectionName;

    public MatchCollectionId(final String owner,
                             final String name)
            throws IllegalArgumentException {

        validateValue("owner", VALID_NAME, owner);
        validateValue("name", VALID_NAME, name);

        this.owner = owner;
        this.name = name;
        this.dbCollectionName = owner + FIELD_SEPARATOR + name;

        if (dbCollectionName.length() > MAX_COLLECTION_NAME_LENGTH) {
            throw new IllegalArgumentException("match db collection name '" + this.dbCollectionName +
                                               "' must be less than " + MAX_COLLECTION_NAME_LENGTH +
                                               " characters therefore the length of the owner and/or " +
                                               "match collection names needs to be reduced");
        }
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDbCollectionName() {
        return dbCollectionName;
    }

    @Override
    public String toString() {
        return "{'owner': '" + owner +
               "', 'name': '" + name + "'}";
    }

    private void validateValue(final String context,
                               final Pattern pattern,
                               final String value)
            throws IllegalArgumentException {

        final Matcher m = pattern.matcher(value);
        if (! m.matches()) {
            throw new IllegalArgumentException("invalid " + context + " '" + value + "' specified");
        }
    }


    // use consecutive underscores to separate fields within a scoped name
    private static final String FIELD_SEPARATOR = "__";

    // valid names are alphanumeric with underscores but no consecutive underscores
    private static final Pattern VALID_NAME = Pattern.compile("([A-Za-z0-9]+(_[A-Za-z0-9])?)++");

    // From http://docs.mongodb.org/manual/reference/limits/
    //   mongodb namespace limit is 123
    //   subtract 5 characters for the database name: "match"
    private static final int MAX_COLLECTION_NAME_LENGTH = 123 - 5;
}