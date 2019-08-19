exports.getRevisionAuthor = async function(padID, rev)
{
    // try to parse the revision number
    if (rev !== undefined) {
        rev = checkValidRev(rev);
    }

    // get the pad
    let pad = await getPadSafe(padID, true);
    let head = pad.getHeadRevisionNumber();

    // the client asked for a special revision
    if (rev !== undefined) {

        // check if this is a valid revision
        if (rev > head) {
            throw new customError("rev is higher than the head revision of the pad", "apierror");
        }

        // get the changeset for this revision
        return pad.getRevisionAuthor(rev);
    }

    // the client wants the latest changeset, lets return it to him
    return pad.getRevisionAuthor(head);
}
exports.getRevisionDate = async function(padID, rev)
{
    // try to parse the revision number
    if (rev !== undefined) {
        rev = checkValidRev(rev);
    }

    // get the pad
    let pad = await getPadSafe(padID, true);
    let head = pad.getHeadRevisionNumber();

    // the client asked for a special revision
    if (rev !== undefined) {

        // check if this is a valid revision
        if (rev > head) {
            throw new customError("rev is higher than the head revision of the pad", "apierror");
        }

        // get the changeset for this revision
        return pad.getRevisionDate(rev);
    }

    // the client wants the latest changeset, lets return it to him
    return pad.getRevisionDate(head);
}