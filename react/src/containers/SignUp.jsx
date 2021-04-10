import React, { useState, useEffect } from "react";
import {
  Button,
  Modal,
  ModalBody,
  ModalFooter,
  Form,
  FormGroup,
  Label,
  Input,
  FormFeedback,
  Alert
} from "reactstrap";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import get from "lodash/get";

import { isEmptyString } from "utils/validators";
import API from "utils/API";
import { updateLoadingAction } from "redux/actions/loading";

function SignUp(props) {
  const [username, setUsername] = useState("");
  const [validationUsernameMsg, setValidationUsernameMsg] = useState(false);
  const [password, setPassword] = useState("");
  const [validationPasswordMsg, setValidationPasswordMsg] = useState(false);
  const [validationMsg, setValidationMsg] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(true);
  useEffect(() => {
    return () => {
      closeModal();
    };
  }, []);

  const handleSubmit = ev => {
    ev.preventDefault();
    const isValid = validateForm();

    if (isValid) {
      const formData = {
        // email,
        password,
        username
      };
      // this.props.onSubmit(formData);
      props.dispatch(updateLoadingAction(true));
      API({
        method: "POST",
        reqUrl: "auth/signup",
        data: formData
      })
        .then(res => {
          props.dispatch(updateLoadingAction(false));
          props.history.push("/login");
        })
        .catch(err => {
          props.dispatch(updateLoadingAction(false));
          const errorResponse = get(err, "response.data", "");
          console.log("errror", err);
          console.log("error response", err.response);
          if (errorResponse === "User already exist") {
            setValidationUsernameMsg(errorResponse);
          } else if (errorResponse) {
            setValidationMsg(errorResponse);
          } else {
            setValidationMsg("Oops! something went wrong");
          }
        });
    }
  };

  const validateForm = touchedElem => {
    let hasInValidPassword = "",
      hasInValidUsername = "";
    if (!touchedElem) {
      // hasInValidEmail = isValidEmail(email);
      hasInValidPassword = isEmptyString(password); // isValidPassword(password);
      hasInValidUsername = isEmptyString(username);
      // setValidationEmailMsg(hasInValidEmail);
      setValidationPasswordMsg(hasInValidPassword);
      setValidationUsernameMsg(hasInValidUsername);
    } else {
      // setValidationEmailMsg(false);
      setValidationPasswordMsg(false);
      setValidationUsernameMsg(false);
    }
    if (
      // !email ||
      !password ||
      // hasInValidEmail ||
      hasInValidPassword ||
      hasInValidUsername
    ) {
      return false;
    }
    return true;
  };

  const handleInputChange = ev => {
    // if (ev.target.name === "email") {
    //   setEmail(ev.target.value);
    // } else
    if (ev.target.name === "username") {
      setUsername(ev.target.value);
    } else {
      setPassword(ev.target.value);
    }
    validateForm(ev.target.name);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };
  return (
    <Modal
      isOpen={isModalOpen}
      toggle={() => {}}
      className="Login"
      centered
      backdrop={false}
    >
      <ModalBody className="Login_body">
        <h2 className="title">ACCOUNT SIGN UP</h2>
        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label for="email">User name</Label>
            <Input
              invalid={validationUsernameMsg ? true : false}
              type="text"
              onChange={handleInputChange}
              name="username"
              maxLength={20}
              minLength={3}
              value={username}
              placeholder="Enter user name"
            />
            <FormFeedback>
              {validationUsernameMsg ? validationUsernameMsg : ""}
            </FormFeedback>
          </FormGroup>

          <FormGroup>
            <Label for="password">Pasword</Label>
            <Input
              invalid={validationPasswordMsg ? true : false}
              type="password"
              onChange={handleInputChange}
              name="password"
              maxLength={100}
              minLength={6}
              value={password}
              placeholder="Enter password"
            />
            <FormFeedback>
              {validationPasswordMsg ? validationPasswordMsg : ""}
            </FormFeedback>
          </FormGroup>
          <Alert color="danger" isOpen={validationMsg ? true : false}>
            {validationMsg}
          </Alert>
          <Button color="secondary" type="submit" className="Login_btn">
            Sign Up
          </Button>
        </Form>
      </ModalBody>
      <ModalFooter className="Login_footer">
        Already have an account ?
        <Link to="/login" style={{ padding: "5px 10px" }}>
          login
        </Link>
      </ModalFooter>
    </Modal>
  );
}
export default connect()(SignUp);
