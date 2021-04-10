import React from "react";
import { connect } from "react-redux";

import Header from "./Header";
import LoadingSpinner from "components/LoadingSpinner";

class Layout extends React.Component {
  render() {
    const { loading } = this.props;
    return (
      <React.Fragment>
        {loading && (
          <div className="loading">
            <LoadingSpinner />
          </div>
        )}
        <Header />
        <div className="main-app">{this.props.children}</div>
      </React.Fragment>
    );
  }
}

const mapStateToProps = state => {
  return {
    loading: state.loadingReducer.loadState,
    favourite_team: state.userReducer.favourite_team
  };
};
export default connect(mapStateToProps)(Layout);
