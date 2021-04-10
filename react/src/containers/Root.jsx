import React, { Component } from "react";
import { Route, Redirect, Switch } from "react-router-dom";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";

import Layout from "./layouts/Layout";
import Home from "./Home";
import Login from "./Login";
import SignUp from "./SignUp";
import AuthorizedRoute from "./AuthorizedRoute";

import { SourceType } from "../redux/constants/ActionTypes";

class Root extends Component {
  render() {
    return (
      <Layout>
        <Switch>
          <Route path="/login" exact component={Login} />
          <Route path="/signup" exact component={SignUp} />
          <Route
            path="/home"
            key="home"
            exact
            render={() => {
              // console.log("loading home")
              return <Home type={SourceType.NEWSAPI_HEADLINES} />;
            }}
          />
          <Route
            path="/search"
            key="search"
            exact
            render={() => {
              // console.log("loading search")
              return <Home type={SourceType.NEWSAPI_SEARCH} />;
            }}
          />
          <AuthorizedRoute
            path="/recommendations"
            key="recommendations"
            type={SourceType.RECOMMENDATIONS}
            exact
            component={Home}
          />
          <AuthorizedRoute
            path="/liked"
            key="liked"
            type={SourceType.LIKED}
            exact
            component={Home}
          />
          <Redirect to="/home" />
        </Switch>
      </Layout>
    );
  }
}

const RootWithRouter = withRouter(props => <Root {...props} />);
export default connect()(RootWithRouter);
