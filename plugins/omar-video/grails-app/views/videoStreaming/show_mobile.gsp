<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Video Player</title>
</head>

<body>

<div class="nav">
  <ul>
  <li><g:link class="home" uri="/">OMAR™ Home</g:link></li>
  </ul>
</div>

<div class="body">

<iphone:content>
  <div style="text-align: center;">

    <video width="480" height="380" controls>
      <source src="${flvUrl}" type='video/mp4; codecs="avc1.42E01E, mp4a.40.2"'>
    </video>

  </div>

</iphone:content>

</div>

</body>
</html>
