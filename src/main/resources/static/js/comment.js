var Comments = React.createClass({
    loadComponentFromServer: function() {
        console.log("loadComponentFromServer");
        this.boardLink = location.origin + "/api/boards/" + this.props.boardSeq;
        $.ajax({
            url: this.boardLink + "/comments"
            ,dataType: "json"
            ,cache: false
            ,success: function(json) {
                console.log(json);
                this.setState({commentResources: json});
            }.bind(this)
            ,error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleDeleteComment: function(comment) {
        if (!confirm("삭제하시겠습니까?")) {
            return;
        }
        $.ajax({
            url: comment._links.self.href
            ,type: "delete"
            ,dataType: "json"
            ,cache: false
            ,success: function() {
                var newCommentResources = this.state.commentResources;
                var newComments = newCommentResources._embedded.comments;
                newComments.splice(newComments.indexOf(comment), 1);
                newCommentResources._embedded.comments = newComments;
                this.setState({commentResources: newCommentResources});
            }.bind(this)
            ,error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleCreateComment: function(comment) {
        console.log("handleCreateComment");
        comment.board = this.boardLink;
        $.ajax({
            url: "/api/comments"
            ,type: "post"
            ,dataType: "json"
            ,contentType: "application/json"
            ,data: JSON.stringify(comment)
            ,cache: false
            ,success: function(json) {
                var newCommentResources = this.state.commentResources;
                var comments = newCommentResources._embedded.comments;
                var newComments = comments.concat([json]);
                newCommentResources._embedded.comments = newComments;
                this.setState({commentResources: newCommentResources});
            }.bind(this)
            ,error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    getInitialState: function () {
        return {commentResources: null};
    },
    componentDidMount: function() {
        console.log("componentDidMount");
        this.loadComponentFromServer();
    },
    render: function() {
        console.log("render");
        console.log(this.state);
        if (this.state.commentResources != null) {
            var handleDeleteComment = this.handleDeleteComment;
            var comments = this.state.commentResources._embedded.comments.map(function (comment) {
                console.log(comment);
                return (
                    <Comment comment={comment} key={comment._links.self.href} onDeleteComment={handleDeleteComment}/>
                );
            });
        }
        return (
            <div className="container">
                {comments}
                <CommentForm onCreateComment={this.handleCreateComment}/>
            </div>
        );
    }
});
var Comment = React.createClass({
    dateFormat: function (timeMillis) {
        var date = new Date(timeMillis);
        return date.dateFormat('yyyy.mm.dd hh:ii:ss');
    },
    handleDelete: function(e) {
        e.preventDefault();
        this.props.onDeleteComment(this.props.comment);
    },
    render: function () {
        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    {this.dateFormat(this.props.comment.regDate)} &nbsp; {this.props.comment.writer} &nbsp;
                    <button className="btn btn-danger btn-xs" onClick={this.handleDelete}>x</button>
                </div>
                <div className="panel-body">
                    {this.props.comment.content}
                </div>
            </div>
        );
    }
});
var CommentForm = React.createClass({
    handleCreate: function(e) {
        var getValueAndCheck = function(ref, msg) {
            var element = ReactDOM.findDOMNode(ref);
            var value = element.value.trim();
            if (value === "") {
                alert(msg);
                element.focus();
            }
            return value;
        };
        var setEmptyValue = function(ref) {
            ReactDOM.findDOMNode(ref).value = "";
        };

        e.preventDefault();
        var writer = getValueAndCheck(this.refs.writer, "작성자를 입력해주세요.");
        if (writer === "") {
            return;
        }
        var content = getValueAndCheck(this.refs.content, "내용을 입력해주세요.");
        if (content === "") {
            return;
        }
        this.props.onCreateComment({writer: writer, content: content});
        setEmptyValue(this.refs.writer);
        setEmptyValue(this.refs.content);
    },
    render: function() {
        return (
            <form onSubmit={this.handleCreate}>
                <div className="panel panel-info">
                    <div className="panel-heading">
                        <input type="text" name="writer" className="form-control" ref="writer" placeholder="작성자"/>
                    </div>
                    <div className="panel-body">
                        <textarea className="form-control" name="content" ref="content" placeholder="댓글내용"></textarea>
                        <p/>
                        <div className="btn-group pull-right">
                            <button type="submit" className="btn btn-primary">등록</button>
                        </div>
                    </div>
                </div>
            </form>
        );
    }
});
ReactDOM.render(
    <Comments boardSeq={window._initParam.boardSeq}/>,
    document.getElementById("comments")
);