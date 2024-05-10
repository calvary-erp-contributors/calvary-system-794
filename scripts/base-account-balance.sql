SELECT
    ta.account_name,
    SUM(te.entry_amount) AS balance
FROM
    transaction_entry te
        JOIN
    transaction_account ta ON te.transaction_account_id = ta.id
GROUP BY
    ta.account_name;
