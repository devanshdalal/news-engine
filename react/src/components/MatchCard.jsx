import React from "react";
import { Button } from "reactstrap";
import API from "utils/API";
import { SourceType } from "../redux/constants/ActionTypes";

class MatchCard extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      urlToImage: "",
      liked: props.ids.includes(props.item.url)
    };
    console.log('ids', props.ids)
  }

  componentDidMount() {
    const { item } = this.props;
    if (item.urlToImage) {
      this.setState({ urlToImage: item.urlToImage });
    }
  }

  async likeHandler(event) {
    const { item } = this.props;
    const { liked } = this.state;
    event.preventDefault();
    event.stopPropagation();
    this.setState({ liked: !liked });
    let reqUrl = "";
    let formData = "";
    if (!liked) {
      // console.log('Liked........')
      reqUrl = "like";
      formData = item;
    } else {
      // console.log('Disiked........')
      reqUrl = "dislike";
      formData = item.id;
    }
    await API({
      method: "POST",
      source: SourceType.LIKED,
      cached: false,
      reqUrl,
      data: formData
    });
    console.log("item", item);
  }

  render() {
    const { description, url, title, content } = this.props.item;
    const { urlToImage, liked } = this.state;
    const descriptionText = description ? description : "";
    const contentText = content ? content : "";
    const body =
      descriptionText.length >= contentText.length
        ? descriptionText
        : contentText;

    return (
      <a
        rel="noopener noreferrer"
        href={url}
        target="_blank"
        key={this.props.keyValue}
        style={this.props.style}
        className="a-no-style"
      >
        <div className="MatchCard">
          <div className="MatchCard_detail">
            <div className="MatchCard_title_bar">
              <strong className="MatchCard_title">{title}</strong>
              <Button
                size="sm"
                color={liked ? "success" : "secondary"}
                className="MatchCard_like"
                onClick={event => this.likeHandler(event)}
              >
                {liked ? "-" : "^"}
              </Button>
            </div>
            <div className="MatchCard_content">
              <div className="MatchCard_desc">{body}</div>
              {urlToImage ? (
                <div className="MatchCard_img">
                  <img src={urlToImage} alt="img" />
                </div>
              ) : (
                <div />
              )}
            </div>
          </div>
        </div>
      </a>
    );
  }
}

export default MatchCard;
