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
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import get from "lodash/get";

import { isEmptyString } from "utils/validators";
import API from "utils/API";
import { updateLoadingAction } from "redux/actions/loading";
import { saveUser } from "redux/actions/user";

function Login(props) {
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
        username,
        password
      };
      // this.props.onSubmit(formData);
      props.dispatch(updateLoadingAction(true));
      API({
        method: "POST",
        reqUrl: "auth/signin",
        data: formData
      })
        .then(res => {
          console.log("res", res);
          delete res.data._id;
          localStorage.setItem("user", JSON.stringify(res.data));
          props.dispatch(saveUser(res.data));
          props.dispatch(updateLoadingAction(false));

          props.history.push("/home");
        })
        .catch(err => {
          props.dispatch(updateLoadingAction(false));
          const error = get(err, "response.data", "");
          if (error) {
            setValidationMsg(error);
          } else {
            setValidationMsg("Oops! something went wrong");
          }
        });
    }
  };

  const validateForm = touchedElem => {
    let hasInvalidUsername = "",
      hasInvalidPassword = "";
    if (!touchedElem) {
      hasInvalidUsername = isEmptyString(username);
      hasInvalidPassword = isEmptyString(password);
      setValidationUsernameMsg(hasInvalidUsername);
      setValidationPasswordMsg(hasInvalidPassword);
    } else {
      setValidationUsernameMsg(false);
      setValidationPasswordMsg(false);
    }
    if (!username || !password || hasInvalidUsername || hasInvalidPassword) {
      return false;
    }
    return true;
  };

  const handleInputChange = ev => {
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
        <h2 className="title">ACCOUNT LOGIN</h2>
        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label for="Username">Username</Label>
            <Input
              invalid={validationUsernameMsg ? true : false}
              type="text"
              onChange={handleInputChange}
              name="username"
              value={username}
              placeholder="Enter username"
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
            Login
          </Button>
        </Form>
      </ModalBody>
      <ModalFooter className="Login_footer">
        Don't have an account yet ?
        <Link to="/signup" style={{ padding: "5px 10px" }}>
          Sign up
        </Link>
      </ModalFooter>
    </Modal>
  );
}

export default connect()(Login);
