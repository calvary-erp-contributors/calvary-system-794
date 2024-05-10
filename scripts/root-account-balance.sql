WITH RECURSIVE account_hierarchy AS (
    -- Base case: select top-level accounts
    SELECT
        id,
        account_name,
        account_name AS root_account_name,
        id AS root_account_id
    FROM
        transaction_account
    WHERE
        parent_account_id IS NULL

    UNION ALL

    -- Recursive case: select sub-accounts
    SELECT
        ta.id,
        ta.account_name,
        ah.root_account_name,
        ah.root_account_id
    FROM
        transaction_account ta
            JOIN
        account_hierarchy ah ON ta.parent_account_id = ah.id
)

SELECT
    ah.root_account_name AS account_name,
    SUM(te.entry_amount) AS balance
FROM
    transaction_entry te
        JOIN
    account_hierarchy ah ON te.transaction_account_id = ah.id
GROUP BY
    ah.root_account_name;
