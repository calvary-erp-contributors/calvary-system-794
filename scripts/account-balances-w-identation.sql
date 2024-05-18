--
-- Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

WITH RECURSIVE account_hierarchy AS (
    -- Base case: select top-level accounts
    SELECT
        id,
        account_name,
        account_name::text AS full_account_name, -- Cast to match overall type
        0 AS level,
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
        CONCAT(ah.full_account_name, ' . ', ta.account_name),
        ah.level + 1,
        ah.root_account_id
    FROM
        transaction_account ta
            JOIN
        account_hierarchy ah ON ta.parent_account_id = ah.id
),

-- Calculate balance for each account
account_balances AS (
   SELECT
       ah.id AS account_id,
       SUM(te.entry_amount) AS balance
   FROM
       transaction_entry te
           JOIN
       account_hierarchy ah ON te.transaction_account_id = ah.id
   WHERE
           te.was_proposed = true
     AND te.was_posted = true
     AND te.was_deleted = false
   GROUP BY
       ah.id
)

SELECT
    CASE WHEN ah.level > 0 THEN CONCAT(REPEAT('    ', ah.level), ah.full_account_name)
         ELSE ah.full_account_name END AS account_name, -- Apply indentation only for sub-accounts
    SUM(ab.balance) AS balance
FROM
    account_hierarchy ah
        LEFT JOIN
    account_balances ab ON ah.id = ab.account_id
GROUP BY
    ah.root_account_id,
    ah.full_account_name,
    ah.level
ORDER BY
    ah.root_account_id,
    ah.level;
