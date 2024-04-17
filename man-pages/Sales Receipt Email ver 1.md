**Calvary ERP Sales Receipt Email Module Documentation**

---

**Introduction**

The Calvary ERP system is implementing an email module to facilitate sending personable email notifications to contributors 
upon the receipt of contributions for various items and projects. 
In order to maintain adherence to design principles, particularly the Open-Closed design pattern, a new entity named 
"salesReceiptEmailPersona" has been introduced. This entity will contain all pertinent details related to email notifications, 
distinct from the primary function of the "dealer" entity, which is specifically designed to identify entities with 
which the religious institution conducts business.

The existing setup of contribution email notification is stuck with the rigid one field design of main-email specification 
on the dealer entity which makes it hard to extend the module to work with the amount of email information to make
the module actually useful, and also an important part of audit information should such be necessary on the contribution
side of the institution's activities. The email module is an important feature on this system and before long you
find that we can really go to town on this implementation if we want the messages we send to also be personable. 
What do we mean? For instance salutations should at least contain the contributor's preferred reference be it their
first name or last name, or the title if such an individual is to be addressed formally. We might also want to include
a caption specific to a persona, or a time-based greeting like "Good evening", if we wish to refrain from salutations that
contain phrases of endearment, or even slogan-based greeting like "forward ever, backwards never". 
Calvary ERP aims to build a system that can include such greetings to specific instances without making it a 
rule for every other entity and have a very personable email notification for the diverse individuals that partake 
contributions and donation to the institution. We also want each email notification to be uniquely identifiable
by introducing an emailIdentifier field that is compliant with UUID specification version 7 allowing emails themselves 
to be an important part of our internal audits.

**SalesReceiptEmailPersona Entity**

The "salesReceiptEmailPersona" entity contains the following fields:

1. **mainEmail**: The primary email address of the contributor.
2. **emailIdentifier**: An identifier ensuring each email is uniquely identifiable, improving audit trail capabilities.
3. **languageKeyCode**: A code indicating the preferred language for communication.
4. **preferredGreeting**: The preferred salutation (e.g., "Dear" or "Hi").
5. **preferredGreetingDesignation**: Additional tags for the preferred salutation (e.g., "first name", "last name", "Member","Elder" , "Pastor", etc).
6. **preferredPrefix**: Preferred title or prefix (e.g., Dr., Prof., Bro., Sis., Ms., ).
7. **preferredSuffix**: Preferred suffix (e.g., Jr., Sr.).
8. **addPrefix**: Whether prefix should be included.
9. **addSuffix**: Whether to include suffix.
10. **timeBasedGreeting**: Whether to include time-based greetings such as "Good morning" or "Good evening."
11. **sloganBasedGreeting**: Whether to include slogan-based greetings (e.g., "Ad astra" or "Numera stellas").
12. **preferredSignature**: Preferred signature (e.g., "Regards," "Faithfully yours," "In His Service", "We Who Wrestle With God"...).
13. **signatureDesignation**: Designation to be included in the signature (e.g., "Treasury Team (Systems Division)," "Treasury Team Calvary ERP").
14. **includeServiceDetails**: Specifies whether to include service details such as service provider, time of receipt, approver, and compiler of reports, along with instructions for queries.
15. **includeMessageOfTheDay**: Whether to include a key verse or message of the day.
16. **includeTreasuryQuote**: Whether to include a treasury quote, encouraging feedback and ideas for resource management improvement.
17. **additionalFields**: Any additional fields that may be required for handling sales receipt notifications.

**Conclusion**

By introducing the "salesReceiptEmailPersona" entity severally from the Dealer entity, the Calvary ERP system ensures adherence to design principles while enabling efficient handling of email notifications for sales receipts. This documentation outlines the purpose and structure of the entity, providing clarity for developers and users alike.
