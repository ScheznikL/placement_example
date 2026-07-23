# App Testing — Use Case Scenarios

When testing mobile applications it is important to have structured test documentation to evaluate the logical consistency of the functionality, visual aspects, user interaction, and overall ergonomics. The use cases below (Tables 3.1 – 3.8) cover the core functionality of the application. Based on the test experiments performed, the app's functionality was found to be logically sound and consistent with the stated requirements.

---

## Table 3.1 — Sign In Use Case

[PASTE IMAGE] <!-- Screenshot: Sign In screen -->

| Field | Value |
|---|---|
| **Name** | Sign In Module |
| **Goal** | Register or sign in a user to the application |
| **Actor** | User |
| **Trigger** | User opens the application |
| **Preconditions** | User must already have a registered account |
| **Success outcome** | User is navigated to the main screen |
| **Failure outcome** | Sign-in not completed; user remains unauthenticated; error message displayed |
| **Systems** | Android OS, Firebase server |

**Steps:**

1. User enters their email address
2. System validates the email format
3. User enters their password
4. System validates the password format
5. User taps the **Sign In** button
6. User is successfully signed into their account

**Exceptions:**
- Email or password in incorrect format → message: *"Invalid credentials"*

---

## Table 3.2 — Registration Use Case

[PASTE IMAGE] <!-- Screenshot: Registration screen -->

| Field | Value |
|---|---|
| **Name** | Registration Module |
| **Goal** | Register a new user in the application |
| **Actor** | User |
| **Trigger** | User opens the application for the first time |
| **Preconditions** | User is opening the application for the first time |
| **Success outcome** | User is navigated to the main screen |
| **Failure outcome** | Registration not completed; user remains unauthenticated; error message displayed |
| **Systems** | Android OS, Firebase server |

**Steps:**

1. User enters their email address
2. System validates the email format
3. User enters a password
4. System validates the password format
5. User confirms the password
6. System verifies that both password entries match
7. User taps the **Register** button
8. User is successfully signed into their account
9. User receives a verification email

**Exceptions:**
- Email or password in incorrect format
- Message: *"User already exists"*

---

## Table 3.3 — Create 3D Model from Image Use Case

[PASTE IMAGE] <!-- Screenshot: Image/photo picker screen -->
[PASTE IMAGE] <!-- Screenshot: Generation in progress -->

| Field | Value |
|---|---|
| **Name** | Create Model from Image Module |
| **Goal** | Generate a 3D model from an image or photo |
| **Actor** | User |
| **Trigger** | User taps **"Model from Image"** (from the home screen) or the attachment icon (from the chat screen) |
| **Preconditions** | User is authenticated and located on the home screen or the chat screen |
| **Success outcome** | Dialog shown asking whether the user wants to view the generated model |
| **Failure outcome** | Error message displayed |
| **Systems** | Android OS, AWS S3, Meshy API server |

**Steps:**

1. User selects a source type — **gallery image** or **camera photo**
2. Image picker dialog is displayed / camera view is opened
3. User selects an image or takes a photo
4. Selected image is shown on screen
5. User optionally enters a model name
6. User taps the **Proceed** button
7. Model generation begins

**Exceptions:**
- Error obtaining presigned URL
- Error generating the model

---

## Table 3.4 — Create 3D Model from Text Use Case

[PASTE IMAGE] <!-- Screenshot: GPT chat conversation screen -->
[PASTE IMAGE] <!-- Screenshot: Final description confirmed, generation started -->

| Field | Value |
|---|---|
| **Name** | Create Model from Text Module |
| **Goal** | Generate a 3D model from a full text description provided by the user |
| **Actor** | User |
| **Trigger** | User taps **"Begin chat to create unique model"** (home screen) or the chat icon in the navigation bar |
| **Preconditions** | User is authenticated and located on the home screen |
| **Success outcome** | Dialog shown asking the user to choose how to view the generated model |
| **Failure outcome** | Dialog not shown; error message received in chat |
| **Systems** | Android OS, OpenAI API server, Meshy API server |

**Steps:**

1. User sends an initial description of the desired model
2. Description appears in the chat
3. AI assistant responds with follow-up questions
4. Conversation continues between the user and the assistant
5. Assistant sends a message containing the **final description**
6. User confirms the description
7. Model generation begins
8. Dialog appears asking the user to choose a viewing mode

**Exceptions:**
- Error obtaining presigned URL
- Error generating the model
- User rejects the description → conversation continues
- Error receiving a response from the assistant
- Error sending a message to the assistant
- Auto-refinement is enabled → additional status message displayed

---

## Table 3.5 — View 3D Model Use Case

[PASTE IMAGE] <!-- Screenshot: 3D Viewer (Filament scene) -->
[PASTE IMAGE] <!-- Screenshot: AR Mode (camera view with model placed) -->

| Field | Value |
|---|---|
| **Name** | View 3D Model Module |
| **Goal** | View a generated 3D model |
| **Actor** | User |
| **Trigger** | User selects a model from the list |
| **Preconditions** | User is on the model list screen |
| **Success outcome** | 3D model is displayed |
| **Failure outcome** | Model not displayed; error message shown; helper dialog opened |
| **Systems** | Android OS, Meshy server |

**Steps:**

1. User selects a model from the list
2. Dialog appears asking whether to open in **View** (3D scene) or **Camera** (AR) mode
3. *Path A:* model is loaded and displayed in an interactive 3D scene
4. *Path B:* model is displayed in the camera view (AR placement)

**Exceptions:**
- Error loading model URL
- Auto-download is enabled → corresponding dialog displayed
- Model lifetime has expired → notification dialog displayed

---

## Table 3.6 — Delete 3D Model Use Case

[PASTE IMAGE] <!-- Screenshot: Model list with long-press selection / delete confirmation dialog -->

| Field | Value |
|---|---|
| **Name** | Delete 3D Model Module |
| **Goal** | Delete a generated 3D model |
| **Actor** | User |
| **Trigger** | User long-presses a model in the list |
| **Preconditions** | User is on the model list screen or the model viewer screen |
| **Success outcome** | One or more 3D models are deleted |
| **Failure outcome** | Model not deleted; error message displayed |
| **Systems** | Android OS |

**Steps:**

1. User long-presses a model to enter selection mode (or selects multiple models)
2. User selects one or more models
3. User taps the delete button
4. Confirmation dialog appears
5. User confirms the deletion
6. Selected model(s) are removed from local storage

**Exceptions:**
- Error deleting model from local database
- Error deleting model from local DataStore

---

## Table 3.7 — Change User Settings Use Case

[PASTE IMAGE] <!-- Screenshot: User profile / settings screen -->

| Field | Value |
|---|---|
| **Name** | Change User Settings Module |
| **Goal** | Change the username, auto-save setting, or auto-refinement setting |
| **Actor** | User |
| **Trigger** | User selects a setting to change |
| **Preconditions** | User is on the profile/cabinet screen |
| **Success outcome** | Settings are updated |
| **Failure outcome** | Settings remain unchanged; changes are only applied locally |
| **Systems** | Android OS, Firestore |

**Steps:**

1. User modifies one of the available settings
2. System sends the updated data to cloud storage
3. UI reflects the applied changes

**Exceptions:**
- Error updating data in cloud storage

---

## Table 3.8 — Change Password Use Case

[PASTE IMAGE] <!-- Screenshot: Re-authentication dialog / password change flow -->

| Field | Value |
|---|---|
| **Name** | Change Password Module |
| **Goal** | Change the user's account password |
| **Actor** | User |
| **Trigger** | User taps the **Change** button next to the "Password" field |
| **Preconditions** | User is on the profile/cabinet screen |
| **Success outcome** | Password changed; user signs in with the new password |
| **Failure outcome** | Password remains unchanged |
| **Systems** | Android OS, Firebase server |

**Steps:**

1. Re-authentication dialog is displayed
2. User's credentials are validated
3. A password-reset link is sent to the email address on the account
4. Confirmation message about the sent email is displayed
5. User is navigated to the sign-in screen
6. User sets a new password via the link
7. User signs in with the new password

**Exceptions:**
- Re-authentication failure — incorrect password entered
- Error sending the password-reset email

---

## Testing Results

All use cases (3.1 – 3.8) were executed. The application's functionality was confirmed to be logically consistent and to meet the stated requirements.
